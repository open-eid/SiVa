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
