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
Image reference from an image map (repository, tag, digest): repository:tag, and @digest appended
when the digest is set (the tag is then a label, the digest is what is pulled). Call with
(list <image map> <tag when the map's is empty>).
*/}}
{{- define "siva-webapp.imageRef" -}}
{{- $image := index . 0 -}}
{{- printf "%s:%s" $image.repository ($image.tag | default (index . 1)) -}}
{{- with $image.digest }}@{{ . }}{{ end -}}
{{- end }}

{{/*
The webapp image: image.repository, image.tag (defaulting to the chart's appVersion, the SiVa
version) and image.digest.
*/}}
{{- define "siva-webapp.image" -}}
{{ include "siva-webapp.imageRef" (list .Values.image .Chart.AppVersion) }}
{{- end }}

{{/*
Refuse secret-looking keys in config.spring, a ConfigMap being no Secret: a leaf key whose name ends
in password, passwd, secret, token, api-key or private-key (kebab- or camelCase) fails the render
pointing at secrets.existingSecret. logging.level names packages and is skipped. Recursive; call
with (dict "path" "" "map" .Values.config.spring).
*/}}
{{- define "siva-webapp.springSecretGuard" -}}
{{- $path := .path -}}
{{- range $key, $value := .map -}}
{{- $full := ternary $key (printf "%s.%s" $path $key) (eq $path "") -}}
{{- if eq $full "logging.level" -}}
{{- else if kindIs "map" $value -}}
{{- include "siva-webapp.springSecretGuard" (dict "path" $full "map" $value) -}}
{{- else if regexMatch `(?i)(password|passwd|secret|token|api-?key|private-?key)$` $key -}}
{{- fail (printf "config.spring.%s names a secret and would land in a ConfigMap: put it in the Secret that secrets.existingSecret names, as a file called %s (README, \"Secrets\")" $full $full) -}}
{{- end -}}
{{- end -}}
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
Non-empty when the store is bridged through object storage (snapshot mode with tsl.snapshot.sync
on). Checks the settings that only make sense together: a remote, one source of rclone
configuration, and none of the shared-volume settings, which would otherwise create or mount a
store nobody uses.
*/}}
{{- define "siva-webapp.syncEnabled" -}}
{{- if and (include "siva-webapp.snapshotMode" .) .Values.tsl.snapshot.sync.enabled -}}
{{- $sync := .Values.tsl.snapshot.sync -}}
{{- if not $sync.remote -}}
{{- fail "tsl.snapshot.sync.remote must name the store in the bucket (<remote>:<bucket>/<prefix>)" -}}
{{- end -}}
{{- if eq (empty $sync.config) (empty $sync.existingSecret) -}}
{{- fail "tsl.snapshot.sync needs exactly one of config (rclone.conf content without secrets) and existingSecret (a Secret with the key rclone.conf)" -}}
{{- end -}}
{{- if or .Values.tsl.snapshot.persistentVolumeClaim.create .Values.tsl.snapshot.volume -}}
{{- fail "tsl.snapshot.sync replaces the shared volume: leave tsl.snapshot.persistentVolumeClaim.create off and tsl.snapshot.volume empty" -}}
{{- end -}}
{{- if .Values.tsl.fetcher.workVolume -}}
{{- fail "tsl.fetcher.workVolume is unused with tsl.snapshot.sync: the working cache travels with the store" -}}
{{- end -}}
true
{{- end -}}
{{- end }}

{{/*
Volume source (without the name) of the snapshot store: a pod-local directory with sync, else the
chart's own claim, or the one given.
*/}}
{{- define "siva-webapp.tslStoreVolumeSource" -}}
{{- if include "siva-webapp.syncEnabled" . -}}
emptyDir:
  {{- with .Values.tsl.snapshot.sync.sizeLimit }}
  sizeLimit: {{ . }}
  {{- else }}
  {}
  {{- end }}
{{- else if .Values.tsl.snapshot.persistentVolumeClaim.create -}}
persistentVolumeClaim:
  claimName: {{ include "siva-webapp.tslClaimName" . }}
{{- else if .Values.tsl.snapshot.volume -}}
{{ toYaml .Values.tsl.snapshot.volume }}
{{- else -}}
{{- fail "tsl.mode snapshot needs a store: set tsl.snapshot.volume (an existing ReadWriteMany claim or a CSI mount), tsl.snapshot.persistentVolumeClaim.create, or tsl.snapshot.sync for a bucket" -}}
{{- end -}}
{{- end }}

{{- define "siva-webapp.syncConfigName" -}}
{{ include "siva-webapp.fullname" . }}-tsl-sync
{{- end }}

{{/*
One rclone container of the sync (tsl.snapshot.sync): the script from the chart's ConfigMap in
the mode given, the store read-write, the rclone configuration read-only from the ConfigMap or
the Secret, a /tmp of its own. Called with a dict: `root`, `mode` (down, up or pod) and `name`.
*/}}
{{- define "siva-webapp.syncContainer" -}}
{{- $sync := .root.Values.tsl.snapshot.sync -}}
- name: {{ .name }}
  image: {{ include "siva-webapp.imageRef" (list $sync.image "") | quote }}
  imagePullPolicy: {{ $sync.image.pullPolicy }}
  command: ["sh", "/etc/siva-tsl-sync/tsl-sync.sh", {{ .mode | quote }}]
  securityContext:
    {{- toYaml (merge (dict "runAsUser" $sync.runAsUser "runAsGroup" $sync.runAsGroup) (deepCopy .root.Values.security.securityContext)) | nindent 4 }}
  env:
    - name: STORE
      value: {{ .root.Values.tsl.snapshot.mountPath | quote }}
    - name: REMOTE
      value: {{ $sync.remote | quote }}
    - name: INTERVAL
      value: {{ $sync.interval | quote }}
    - name: KEEP
      value: {{ $sync.keep | quote }}
    - name: RCLONE_CONFIG
      value: /etc/rclone/rclone.conf
    - name: RCLONE_CACHE_DIR
      value: /tmp/rclone
    {{- with $sync.env }}
    {{- toYaml . | nindent 4 }}
    {{- end }}
  {{- with $sync.resources }}
  resources:
    {{- toYaml . | nindent 4 }}
  {{- end }}
  volumeMounts:
    - name: tsl-store
      mountPath: {{ .root.Values.tsl.snapshot.mountPath }}
    - name: tsl-sync
      mountPath: /etc/siva-tsl-sync
      readOnly: true
    - name: rclone-config
      mountPath: /etc/rclone
      readOnly: true
    - name: tsl-sync-tmp
      mountPath: /tmp
{{- end }}

{{/*
The volumes the sync containers mount besides the store.
*/}}
{{- define "siva-webapp.syncVolumes" -}}
{{- $sync := .Values.tsl.snapshot.sync -}}
- name: tsl-sync
  configMap:
    name: {{ include "siva-webapp.syncConfigName" . }}
    items:
      - key: tsl-sync.sh
        path: tsl-sync.sh
- name: rclone-config
  {{- if $sync.existingSecret }}
  secret:
    secretName: {{ $sync.existingSecret }}
    items:
      - key: rclone.conf
        path: rclone.conf
    {{- /* Readable by the sync containers' uid, which is not necessarily the Secret's; the mount is read-only. */}}
    defaultMode: 0444
  {{- else }}
  configMap:
    name: {{ include "siva-webapp.syncConfigName" . }}
    items:
      - key: rclone.conf
        path: rclone.conf
  {{- end }}
- name: tsl-sync-tmp
  emptyDir:
    sizeLimit: 64Mi
{{- end }}

{{/*
Whether the kubelet runs a sidecar as a restartable init container (Kubernetes 1.29+, where it
starts before the app container and stops after it); below that the sidecar is a plain container.
*/}}
{{- define "siva-webapp.nativeSidecars" -}}
{{- if semverCompare ">=1.29.0-0" .Capabilities.KubeVersion.Version }}true{{ end }}
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
The fetcher's working cache: its own volume when given, else a directory inside the store (with
sync always the store, which carries it to the bucket and back).
*/}}
{{- define "siva-webapp.fetcherWorkDir" -}}
{{- if and .Values.tsl.fetcher.workVolume (not (include "siva-webapp.syncEnabled" .)) -}}/var/siva/tsl-work{{- else -}}{{ .Values.tsl.snapshot.mountPath }}/work{{- end -}}
{{- end }}

{{/*
The fetcher pods' ServiceAccount: their own by default (an identity that may write the store is
never the webapp's), a named one, or the webapp's.
*/}}
{{- define "siva-webapp.fetcherServiceAccountName" -}}
{{- $sa := .Values.tsl.fetcher.serviceAccount -}}
{{- if $sa.create -}}
{{- default (include "siva-webapp.fetcherName" .) $sa.name }}
{{- else -}}
{{- default (include "siva-webapp.serviceAccountName" .) $sa.name }}
{{- end -}}
{{- end }}

{{/*
Pod template (metadata + spec) shared by the fetch CronJob and the bootstrap Job: the webapp
image and settings (ConfigMap, env, JVM flags, restricted security context) plus SiVa's
tsl-fetch profile, the store mounted read-write and no listening port. The Secret, extra volumes
and mounts are the fetcher's own (tsl.fetcher.*), never the webapp's.
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
  serviceAccountName: {{ include "siva-webapp.fetcherServiceAccountName" . }}
  automountServiceAccountToken: {{ .Values.security.automountServiceAccountToken }}
  {{- with .Values.security.podSecurityContext }}
  securityContext:
    {{- toYaml . | nindent 4 }}
  {{- end }}
  {{- if include "siva-webapp.syncEnabled" . }}
  {{- /* The uploader waits for the fetcher's JVM to exit; that is the one signal a container has
         of another's end without the API, and the store is at rest once it is gone. */}}
  shareProcessNamespace: true
  initContainers:
    {{- include "siva-webapp.syncContainer" (dict "root" . "mode" "down" "name" "tsl-sync-down") | nindent 4 }}
  {{- end }}
  containers:
    - name: tsl-fetch
      image: "{{ include "siva-webapp.image" . }}"
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
        - name: SIVA_TSL_FETCHER_PERIOD
          value: {{ .Values.tsl.fetcher.period | quote }}
        # A fetch is for downloading: never let DSS's file cache (fresh for an hour by default)
        # answer instead, or a period under an hour fetches every other run and the download
        # times the pods publish lag by that hour.
        - name: SIVA_TSL_LOADER_ONLINECACHEEXPIRATIONTIME
          value: PT0S
        {{- with include "siva-webapp.territoryEnv" . | trim }}
        {{- . | nindent 8 }}
        {{- end }}
        {{- with include "siva-webapp.trustEnv" . | trim }}
        {{- . | nindent 8 }}
        {{- end }}
        {{- with include "siva-webapp.secretsEnv" (dict "root" . "secret" .Values.tsl.fetcher.existingSecret) | trim }}
        {{- . | nindent 8 }}
        {{- end }}
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
        {{- with include "siva-webapp.trustVolumeMounts" . | trim }}
        {{- . | nindent 8 }}
        {{- end }}
        {{- with include "siva-webapp.secretsVolumeMounts" (dict "root" . "secret" .Values.tsl.fetcher.existingSecret) | trim }}
        {{- . | nindent 8 }}
        {{- end }}
        {{- with .Values.tsl.fetcher.extraVolumeMounts }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
    {{- if include "siva-webapp.syncEnabled" . }}
    {{- include "siva-webapp.syncContainer" (dict "root" . "mode" "up" "name" "tsl-sync-up") | nindent 4 }}
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
    {{- if include "siva-webapp.syncEnabled" . }}
    {{- include "siva-webapp.syncVolumes" . | nindent 4 }}
    {{- end }}
    {{- with .Values.tsl.fetcher.workVolume }}
    - name: tsl-work
      {{- toYaml . | nindent 6 }}
    {{- end }}
    {{- with include "siva-webapp.trustVolumes" . | trim }}
    {{- . | nindent 4 }}
    {{- end }}
    {{- with include "siva-webapp.secretsVolumes" (dict "root" . "secret" .Values.tsl.fetcher.existingSecret) | trim }}
    {{- . | nindent 4 }}
    {{- end }}
    {{- with .Values.tsl.fetcher.extraVolumes }}
    {{- toYaml . | nindent 4 }}
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
  {{- with .dnsTo }}
  to:
    {{- toYaml . | nindent 4 }}
  {{- end }}
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

{{/*
Seconds of an ISO-8601 duration in the forms the trust-list settings use: PnD, PTnH, PTnM, PTnS and
combinations (P1DT12H). Anything else fails the render with the value's name. Call with
(list "<value name>" "<duration>").
*/}}
{{- define "siva-webapp.isoSeconds" -}}
{{- $name := index . 0 -}}
{{- $value := index . 1 | toString -}}
{{- $re := `^P(?:([0-9]+)D)?(?:T(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?)?$` -}}
{{- if or (not (regexMatch $re $value)) (eq $value "P") (eq $value "PT") -}}
{{- fail (printf "%s must be an ISO-8601 duration like PT6H or P7D, not %q" $name $value) -}}
{{- end -}}
{{- $d := regexReplaceAll $re $value "${1}" | default "0" | atoi -}}
{{- $h := regexReplaceAll $re $value "${2}" | default "0" | atoi -}}
{{- $m := regexReplaceAll $re $value "${3}" | default "0" | atoi -}}
{{- $s := regexReplaceAll $re $value "${4}" | default "0" | atoi -}}
{{- add (mul $d 86400) (mul $h 3600) (mul $m 60) $s -}}
{{- end }}

{{/*
The trust-list refresh period P in seconds (ADR 0004): tsl.fetcher.period in snapshot mode, at most
seven days; one day in online mode, where each pod refreshes on siva.tsl.loader.schedulerCron
(03:00 daily by default).
*/}}
{{- define "siva-webapp.tslPeriodSeconds" -}}
{{- if include "siva-webapp.snapshotMode" . -}}
{{- $p := include "siva-webapp.isoSeconds" (list "tsl.fetcher.period" .Values.tsl.fetcher.period) | atoi -}}
{{- if or (le $p 0) (gt $p 604800) -}}
{{- fail (printf "tsl.fetcher.period must be more than zero and at most P7D (one week), not %q: trusted lists can run out within three months of issue, a longer fetch leaves too little time to notice" .Values.tsl.fetcher.period) -}}
{{- end -}}
{{- $p -}}
{{- else -}}
86400
{{- end -}}
{{- end }}

{{/*
Cron schedule of the fetch: tsl.fetcher.schedule when set, else derived from the period. Derivable:
minutes dividing an hour, hours dividing a day, one day (03:00) and seven days (Mondays 03:00).
*/}}
{{- define "siva-webapp.fetcherSchedule" -}}
{{- if .Values.tsl.fetcher.schedule -}}
{{- .Values.tsl.fetcher.schedule -}}
{{- else -}}
{{- $p := include "siva-webapp.tslPeriodSeconds" . | atoi -}}
{{- if and (lt $p 3600) (eq (mod $p 60) 0) (eq (mod 3600 $p) 0) -}}
{{- printf "*/%d * * * *" (div $p 60) -}}
{{- else if eq $p 3600 -}}
0 * * * *
{{- else if and (lt $p 86400) (eq (mod $p 3600) 0) (eq (mod 86400 $p) 0) -}}
{{- printf "0 */%d * * *" (div $p 3600) -}}
{{- else if eq $p 86400 -}}
0 3 * * *
{{- else if eq $p 604800 -}}
0 3 * * 1
{{- else -}}
{{- fail (printf "tsl.fetcher.period %q has no derived schedule (minutes dividing an hour, hours dividing a day, P1D or P7D); set tsl.fetcher.schedule to match it" .Values.tsl.fetcher.period) -}}
{{- end -}}
{{- end -}}
{{- end }}

{{/*
siva.tsl.loader.snapshotMaxAge: tsl.snapshot.maxAge when set, nothing for "none", else
max(4P, 48 h) in hours.
*/}}
{{- define "siva-webapp.snapshotMaxAge" -}}
{{- $v := .Values.tsl.snapshot.maxAge | toString -}}
{{- if eq $v "none" -}}
{{- else if $v -}}
{{- $v -}}
{{- else -}}
{{- $p := include "siva-webapp.tslPeriodSeconds" . | atoi -}}
{{- printf "PT%dH" (div (max (mul 4 $p) 172800) 3600) -}}
{{- end -}}
{{- end }}

{{/*
Seconds as a short human text for alert summaries: "14 d 1 h", "13 h", "1 h 30 min", "30 min".
*/}}
{{- define "siva-webapp.humanSeconds" -}}
{{- $s := . | int -}}
{{- $d := div $s 86400 -}}
{{- $h := div (mod $s 86400) 3600 -}}
{{- $m := div (mod $s 3600) 60 -}}
{{- $parts := list -}}
{{- if $d }}{{ $parts = append $parts (printf "%d d" $d) }}{{ end -}}
{{- if $h }}{{ $parts = append $parts (printf "%d h" $h) }}{{ end -}}
{{- if and $m (not $d) }}{{ $parts = append $parts (printf "%d min" $m) }}{{ end -}}
{{- join " " $parts -}}
{{- end }}

{{/*
The countries SiVa trusts and requires, when set in values (empty: SiVa's own defaults, which the
test profile replaces). Webapp and fetcher get the same, or the pods look for lists the fetcher
never downloaded.
*/}}
{{- define "siva-webapp.territoryEnv" -}}
{{- with .Values.tsl.territories.trusted }}
- name: SIVA_TSL_LOADER_TRUSTEDTERRITORIES
  value: {{ join "," . | quote }}
{{- end }}
{{- with .Values.tsl.territories.mandatory }}
- name: SIVA_TSL_LOADER_MANDATORYTERRITORIES
  value: {{ join "," . | quote }}
{{- end }}
{{- end }}

{{/*
Trust material (trust.*): non-empty when PEM files are given (`pem`) or an existing ConfigMap or
Secret is named; exactly one source, and at least one file wired, or the render fails.
*/}}
{{- define "siva-webapp.trustEnabled" -}}
{{- $t := .Values.trust -}}
{{- $sources := 0 -}}
{{- if $t.pem }}{{ $sources = add $sources 1 }}{{ end -}}
{{- if $t.existingConfigMap }}{{ $sources = add $sources 1 }}{{ end -}}
{{- if $t.existingSecret }}{{ $sources = add $sources 1 }}{{ end -}}
{{- if gt $sources 1 -}}
{{- fail "trust: set one of pem, existingConfigMap and existingSecret, not several" -}}
{{- end -}}
{{- if eq $sources 1 -}}
{{- if and (not $t.tslCa) (not $t.lotlSigners) -}}
{{- fail "trust: material is given but neither trust.tslCa nor trust.lotlSigners names a file in it" -}}
{{- end -}}
{{- range $file := list $t.tslCa $t.lotlSigners -}}
{{- if and $file $t.pem (not (hasKey $t.pem $file)) -}}
{{- fail (printf "trust.pem has no file %q (named by trust.tslCa / trust.lotlSigners)" $file) -}}
{{- end -}}
{{- end -}}
true
{{- end -}}
{{- end }}

{{/*
Name of the ConfigMap or Secret holding the PEM files (the chart's own when `pem` is given).
*/}}
{{- define "siva-webapp.trustSourceName" -}}
{{- if .Values.trust.pem -}}
{{ include "siva-webapp.fullname" . }}-trust
{{- else -}}
{{ coalesce .Values.trust.existingConfigMap .Values.trust.existingSecret }}
{{- end -}}
{{- end }}

{{/*
Spring SSL bundles over the mounted files and the SiVa properties that select them, as
environment (relaxed binding: RELOADONUPDATE is reload-on-update, SSLBUNDLE is sslBundle).
*/}}
{{- define "siva-webapp.trustEnv" -}}
{{- if include "siva-webapp.trustEnabled" . }}
{{- $t := .Values.trust }}
{{- with $t.tslCa }}
- name: SPRING_SSL_BUNDLE_PEM_TSL_TRUSTSTORE_CERTIFICATE
  value: {{ printf "file:%s/%s" $t.mountPath . | quote }}
- name: SPRING_SSL_BUNDLE_PEM_TSL_RELOADONUPDATE
  value: "true"
- name: SIVA_TSL_LOADER_SSLBUNDLE
  value: tsl
{{- end }}
{{- with $t.lotlSigners }}
- name: SPRING_SSL_BUNDLE_PEM_LOTL_TRUSTSTORE_CERTIFICATE
  value: {{ printf "file:%s/%s" $t.mountPath . | quote }}
- name: SPRING_SSL_BUNDLE_PEM_LOTL_RELOADONUPDATE
  value: "true"
- name: SIVA_TSL_LOADER_LOTLBUNDLE
  value: lotl
{{- end }}
{{- if include "siva-webapp.trustBinding" . }}
- name: SERVICE_BINDING_ROOT
  value: /platform/bindings
{{- end }}
{{- end }}
{{- end }}

{{/*
Non-empty when the CA file is also handed to the JVM through a Paketo ca-certificates binding.
*/}}
{{- define "siva-webapp.trustBinding" -}}
{{- if and (include "siva-webapp.trustEnabled" .) .Values.trust.caCertificatesBinding .Values.trust.tslCa }}true{{ end }}
{{- end }}

{{- define "siva-webapp.trustVolumeMounts" -}}
{{- if include "siva-webapp.trustEnabled" . }}
- name: trust
  mountPath: {{ .Values.trust.mountPath }}
  readOnly: true
{{- if include "siva-webapp.trustBinding" . }}
- name: trust-binding
  mountPath: /platform/bindings/ca-certificates
  readOnly: true
{{- end }}
{{- end }}
{{- end }}

{{/*
The PEM files as a volume and, with the binding, a projected directory in the service-binding
layout: a `type` file (from the chart's `<release>-trust-binding` ConfigMap) next to the CA file.
*/}}
{{- define "siva-webapp.trustVolumes" -}}
{{- if include "siva-webapp.trustEnabled" . }}
{{- $name := include "siva-webapp.trustSourceName" . }}
{{- $kind := ternary "secret" "configMap" (not (empty .Values.trust.existingSecret)) }}
- name: trust
  {{- if eq $kind "secret" }}
  secret:
    secretName: {{ $name }}
  {{- else }}
  configMap:
    name: {{ $name }}
  {{- end }}
{{- if include "siva-webapp.trustBinding" . }}
- name: trust-binding
  projected:
    sources:
      - configMap:
          name: {{ include "siva-webapp.fullname" . }}-trust-binding
          items:
            - key: type
              path: type
      - {{ $kind }}:
          name: {{ $name }}
          items:
            - key: {{ .Values.trust.tslCa }}
              path: {{ .Values.trust.tslCa }}
{{- end }}
{{- end }}
{{- end }}

{{/*
podFailurePolicy of the fetch Jobs. A run that ended below the readiness threshold (exit 1) or
published from a last copy because a download failed (exit 3) fails for a reason a retry seconds
later cannot change, so the Job fails at once instead of running backoffLimit more pods (and, for
3, publishing as many snapshots); a store write failure (2) and container errors keep the retries.
Needs restartPolicy Never and Kubernetes 1.26+ (the chart's floor).
*/}}
{{- define "siva-webapp.fetcherFailurePolicy" -}}
podFailurePolicy:
  rules:
    - action: FailJob
      onExitCodes:
        containerName: tsl-fetch
        operator: In
        values: [1, 3]
{{- end }}

{{/*
Secrets as files: the named Secret mounted read-only at secrets.mountPath and imported as a Spring
config tree, so every key is a file whose name is the property it sets
(`siva.signature-service.pkcs12.password`) and nothing reaches the environment. Called with a
dict: `root` (the top-level context) and `secret` (the Secret name, empty for none), so the
webapp and the fetcher can name different Secrets.
*/}}
{{- define "siva-webapp.secretsEnv" -}}
{{- if .secret }}
- name: SPRING_CONFIG_IMPORT
  value: {{ printf "configtree:%s/" (trimSuffix "/" .root.Values.secrets.mountPath) | quote }}
{{- end }}
{{- end }}

{{- define "siva-webapp.secretsVolumeMounts" -}}
{{- if .secret }}
- name: secrets
  mountPath: {{ .root.Values.secrets.mountPath }}
  readOnly: true
{{- end }}
{{- end }}

{{- define "siva-webapp.secretsVolumes" -}}
{{- if .secret }}
- name: secrets
  secret:
    secretName: {{ .secret }}
    {{- /* Readable by any uid on purpose: the pods run non-root without an fsGroup, so a 0400 file
           would be unreadable; the mount is read-only and the pod is the boundary. */}}
    defaultMode: 0444
{{- end }}
{{- end }}
