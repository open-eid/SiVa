{{/*
Expand the name of the chart.
*/}}
{{- define "siva-webapp.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "siva-webapp.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "siva-webapp.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "siva-webapp.labels" -}}
helm.sh/chart: {{ include "siva-webapp.chart" . }}
{{ include "siva-webapp.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "siva-webapp.selectorLabels" -}}
app.kubernetes.io/name: {{ include "siva-webapp.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "siva-webapp.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "siva-webapp.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Whether the actuator runs on its own port (management port differs from the traffic port).
*/}}
{{- define "siva-webapp.managementSplit" -}}
{{- if ne (int .Values.runtime.managementPort) (int .Values.service.containerPort) }}true{{ end }}
{{- end }}

{{/*
Path of a probe on the traffic port. An explicit value wins; otherwise Spring Boot's /readyz and
/livez when the actuator is split (management.endpoint.health.probes.add-additional-paths publishes
the two probe groups on the traffic port only when there is a separate management port), or the
groups under /monitoring/health when the actuator shares the traffic port.
Usage: include "siva-webapp.probePath" (dict "root" $ "group" "readiness" "path" .path)
*/}}
{{- define "siva-webapp.probePath" -}}
{{- if .path -}}
{{- if and (include "siva-webapp.managementSplit" .root) (hasPrefix "/monitoring/" .path) -}}
{{- fail (printf "probes.%s.path %q is under /monitoring, which the traffic port does not serve while the actuator is on its own port (runtime.managementPort); leave the path empty for /readyz and /livez" .group .path) -}}
{{- end -}}
{{ .path }}
{{- else if include "siva-webapp.managementSplit" .root -}}
{{ ternary "/readyz" "/livez" (eq .group "readiness") }}
{{- else -}}
/monitoring/health/{{ .group }}
{{- end -}}
{{- end }}

{{/*
In-cluster URL of the health endpoint (management Service when split, traffic Service otherwise).
*/}}
{{- define "siva-webapp.healthUrl" -}}
{{- if include "siva-webapp.managementSplit" . -}}
http://{{ include "siva-webapp.fullname" . }}-management:{{ .Values.runtime.managementPort }}/monitoring/health
{{- else -}}
http://{{ include "siva-webapp.fullname" . }}:{{ .Values.service.port }}/monitoring/health
{{- end }}
{{- end }}

{{/*
Trust-list mode: "online" (each pod downloads) or "snapshot" (the fetcher CronJob publishes
into a shared volume, pods read it). Non-empty when snapshot.
*/}}
{{- define "siva-webapp.snapshotMode" -}}
{{- if not (has .Values.tsl.mode (list "online" "snapshot")) -}}
{{- fail (printf "tsl.mode must be online or snapshot, not %q" .Values.tsl.mode) -}}
{{- end -}}
{{- if eq .Values.tsl.mode "snapshot" }}true{{ end }}
{{- end }}

{{- define "siva-webapp.tslClaimName" -}}
{{ include "siva-webapp.fullname" . }}-tsl
{{- end }}

{{/*
Volume source (without the name) of the snapshot store: the chart's own claim, or the one given.
*/}}
{{- define "siva-webapp.tslStoreVolumeSource" -}}
{{- if .Values.tsl.snapshot.persistentVolumeClaim.create -}}
persistentVolumeClaim:
  claimName: {{ include "siva-webapp.tslClaimName" . }}
{{- else if .Values.tsl.snapshot.volume -}}
{{ toYaml .Values.tsl.snapshot.volume }}
{{- else -}}
{{- fail "tsl.mode snapshot needs a store: set tsl.snapshot.volume (an existing ReadWriteMany claim or a CSI mount) or tsl.snapshot.persistentVolumeClaim.create" -}}
{{- end -}}
{{- end }}

{{- define "siva-webapp.fetcherName" -}}
{{ include "siva-webapp.fullname" . }}-tsl-fetch
{{- end }}

{{/*
The fetcher pods carry their own app.kubernetes.io/name so that the Service, PDB, HPA and
NetworkPolicy selectors (name + instance) never pick them up.
*/}}
{{- define "siva-webapp.fetcherSelectorLabels" -}}
app.kubernetes.io/name: {{ include "siva-webapp.name" . }}-tsl-fetch
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "siva-webapp.fetcherLabels" -}}
helm.sh/chart: {{ include "siva-webapp.chart" . }}
{{ include "siva-webapp.fetcherSelectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/component: tsl-fetch
{{- end }}

{{/*
The fetcher's working cache: its own volume when given, else a directory inside the store.
*/}}
{{- define "siva-webapp.fetcherWorkDir" -}}
{{- if .Values.tsl.fetcher.workVolume -}}/var/siva/tsl-work{{- else -}}{{ .Values.tsl.snapshot.mountPath }}/work{{- end -}}
{{- end }}

{{/*
Pod template (metadata + spec) shared by the fetch CronJob and the bootstrap Job: the webapp
image and settings (ConfigMap, env, secret, JVM flags, restricted security context) plus SiVa's
tsl-fetch profile, the store mounted read-write and no listening port.
*/}}
{{- define "siva-webapp.fetcherPodTemplate" -}}
metadata:
  annotations:
    checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
    {{- with .Values.podAnnotations }}
    {{- toYaml . | nindent 4 }}
    {{- end }}
  labels:
    {{- include "siva-webapp.fetcherLabels" . | nindent 4 }}
    {{- with .Values.podLabels }}
    {{- toYaml . | nindent 4 }}
    {{- end }}
spec:
  restartPolicy: Never
  {{- with .Values.image.pullSecrets }}
  imagePullSecrets:
    {{- toYaml . | nindent 4 }}
  {{- end }}
  serviceAccountName: {{ include "siva-webapp.serviceAccountName" . }}
  automountServiceAccountToken: {{ .Values.security.automountServiceAccountToken }}
  {{- with .Values.security.podSecurityContext }}
  securityContext:
    {{- toYaml . | nindent 4 }}
  {{- end }}
  containers:
    - name: tsl-fetch
      image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
      imagePullPolicy: {{ .Values.image.pullPolicy }}
      {{- with .Values.security.securityContext }}
      securityContext:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      env:
        - name: SPRING_CONFIG_ADDITIONAL_LOCATION
          value: "file:{{ .Values.config.mountPath }}/application.yml"
        - name: SPRING_PROFILES_INCLUDE
          value: tsl-fetch
        - name: SIVA_TSL_LOADER_LOADFROMCACHE
          value: "false"
        - name: SIVA_TSL_LOADER_CACHEDIRECTORY
          value: {{ include "siva-webapp.fetcherWorkDir" . | quote }}
        - name: SIVA_TSL_FETCHER_OUTPUTDIRECTORY
          value: {{ .Values.tsl.snapshot.mountPath | quote }}
        - name: SIVA_TSL_FETCHER_KEEPSNAPSHOTS
          value: {{ .Values.tsl.fetcher.keepSnapshots | quote }}
        {{- with .Values.runtime.javaToolOptions }}
        - name: JAVA_TOOL_OPTIONS
          value: {{ . | quote }}
        {{- end }}
        {{- with .Values.env }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
        {{- with .Values.tsl.fetcher.env }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
      {{- with .Values.tsl.fetcher.existingSecret }}
      envFrom:
        - secretRef:
            name: {{ . }}
      {{- end }}
      {{- with .Values.tsl.fetcher.resources }}
      resources:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      volumeMounts:
        - name: config
          mountPath: {{ .Values.config.mountPath }}
          readOnly: true
        - name: tmp
          mountPath: /tmp
        - name: tsl-store
          mountPath: {{ .Values.tsl.snapshot.mountPath }}
        {{- if .Values.tsl.fetcher.workVolume }}
        - name: tsl-work
          mountPath: /var/siva/tsl-work
        {{- end }}
  volumes:
    - name: config
      configMap:
        name: {{ include "siva-webapp.fullname" . }}
    - name: tmp
      emptyDir:
        {{- with .Values.tmpSizeLimit }}
        sizeLimit: {{ . }}
        {{- else }}
        {}
        {{- end }}
    - name: tsl-store
      {{- include "siva-webapp.tslStoreVolumeSource" . | nindent 6 }}
    {{- with .Values.tsl.fetcher.workVolume }}
    - name: tsl-work
      {{- toYaml . | nindent 6 }}
    {{- end }}
  {{- with .Values.nodeSelector }}
  nodeSelector:
    {{- toYaml . | nindent 4 }}
  {{- end }}
  {{- with .Values.tolerations }}
  tolerations:
    {{- toYaml . | nindent 4 }}
  {{- end }}
{{- end }}

{{/*
Egress rules shared by the webapp and the fetcher NetworkPolicies (network.networkPolicy.egress).
*/}}
{{- define "siva-webapp.egressRules" -}}
{{- with .Values.network.networkPolicy.egress }}
{{- if .dns }}
- ports:
    - port: 53
      protocol: UDP
    - port: 53
      protocol: TCP
{{- end }}
- ports:
    {{- range .ports }}
    - port: {{ . }}
      protocol: TCP
    {{- end }}
  {{- with .to }}
  to:
    {{- toYaml . | nindent 4 }}
  {{- end }}
{{- with .additionalRules }}
{{ toYaml . }}
{{- end }}
{{- end }}
{{- end }}
