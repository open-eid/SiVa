# siva-webapp

![Version: 0.8.3](https://img.shields.io/badge/Version-0.8.3-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 3.10.3](https://img.shields.io/badge/AppVersion-3.10.3-informational?style=flat-square)

Reference deployment of [SiVa](https://github.com/open-eid/SiVa), the Estonian digital-signature
validation service, on Kubernetes. Like the project's docker-compose file it is a reference, not a
product: best-effort, no support promise, build and run it yourself.

Licence: EUPL 1.1, the same as the SiVa repository this chart belongs with.

## Before you install

- **Build your own image.** SiVa publishes source only. `./mvnw spring-boot:build-image` in the
  SiVa checkout produces a Paketo image of `siva-webapp`; push it to a registry your cluster can
  pull from and set `image.repository` / `image.tag`.
- **Egress.** Each pod downloads the EU LOTL and the member-state trust lists at startup and on a
  nightly cron. Allow HTTPS egress to those hosts, and to the OCSP/TSA responders of the
  certificates you validate.
- **Trust material.** This iteration uses the truststores shipped on the application classpath.
  Supplying your own is not wired yet (`trust.*` is a placeholder).

```sh
helm install siva ./helm/siva-webapp \
  --set image.repository=registry.example.invalid/siva-webapp --set-string image.tag=3.10.3
```

`image.tag` and `env[].value` are strings; use `--set-string` for numeric-looking values (a git
short sha such as `1234567` would otherwise be parsed as a number and rejected by the values
schema).

## Values

Generated from the comments in `values.yaml` by helm-docs (1.14; run `helm-docs` in this
directory); edit the comments, not this table.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` | Affinity rules, as given. |
| config.applicationYaml | string | actuator probes enabled | Spring Boot configuration override, rendered into a ConfigMap (same mechanism as the project's docker-compose.yaml). Properties here win over the application.yml on the classpath; the `siva.*` keys are documented in the SiVa deployment guide (docs/siva3/deployment_guide.md). Put environment-specific settings in a private overlay. |
| config.mountPath | string | `"/etc/siva-webapp"` | Where the Spring Boot configuration override is mounted; the container gets SPRING_CONFIG_ADDITIONAL_LOCATION=file:<mountPath>/application.yml. |
| env | list | `[]` | Extra environment variables for the container (plain Kubernetes EnvVar entries). Spring relaxed binding applies, e.g. `{name: SIVA_TSL_LOADER_TRUSTEDTERRITORIES, value: "EE,LV"}` (values are strings: use `--set-string` on the command line). Do not set JAVA_TOOL_OPTIONS or the runtime.* variables here: a later duplicate wins and silently replaces the chart's value; use runtime.* instead. |
| envFrom.existingSecret | string | `""` | Name of an existing Secret whose keys become environment variables (passwords, keystore passphrases). Not created by the chart. |
| fullnameOverride | string | `""` | Override the full resource name (release name + chart name). |
| image.pullPolicy | string | `"IfNotPresent"` | Pull policy. |
| image.pullSecrets | list | `[]` | imagePullSecrets, e.g. `[{name: my-registry-credentials}]`. |
| image.repository | string | `"siva-webapp"` | Image to run. SiVa publishes no image: build one with `./mvnw spring-boot:build-image` (Paketo buildpacks), push it to a registry your cluster can pull from and set this to it. |
| image.tag | string | `""` | Image tag; empty means the chart's appVersion (the SiVa release this chart was written for). Numeric-looking tags need `--set-string`. |
| monitoring.grafanaDashboard.annotations | object | `{}` | Annotations, e.g. `{grafana_folder: SiVa}`. |
| monitoring.grafanaDashboard.enabled | bool | `false` | Dashboard ConfigMap for the Grafana sidecar (kube-prometheus-stack picks up the label). Generic by design: one dashboard, namespace and pod variables select the release. The latency panel shows mean and max; for percentiles set `management.metrics.distribution.percentiles-histogram.http.server.requests: true` in config.applicationYaml (adds histogram buckets per uri) and use http_server_requests_seconds_bucket. |
| monitoring.grafanaDashboard.labels | object | `{"grafana_dashboard":"1"}` | Labels the sidecar selects dashboards by. |
| monitoring.grafanaDashboard.namespace | string | `""` | Namespace to create it in (empty: the release namespace). |
| monitoring.prometheusRule.additionalRules | list | `[]` | Extra rules appended to the group, as given. |
| monitoring.prometheusRule.enabled | bool | `false` | Alert rules (PrometheusRule CRD): SivaNotReady, SivaValidationErrorRate, SivaRestarts, SivaOomKilled. They use Spring Boot's http_server_requests metrics and kube-state-metrics. |
| monitoring.prometheusRule.labels | object | `{}` | Labels your Prometheus selects PrometheusRules by. |
| monitoring.prometheusRule.namespace | string | `""` | Namespace to create it in (empty: the release namespace). |
| monitoring.prometheusRule.pendingRules | bool | `false` | Rules for signals the application does not publish yet: oversized requests (answered 400 today, no 413), trust-list age and territory state, OCSP/TSA failures. Off until it does. |
| monitoring.serviceMonitor.enabled | bool | `false` | Prometheus-operator ServiceMonitor scraping /monitoring/prometheus on the management Service (the traffic Service when the actuator is not split). Needs the CRD. |
| monitoring.serviceMonitor.interval | string | `"30s"` | Scrape interval (Prometheus duration). |
| monitoring.serviceMonitor.labels | object | `{}` | Labels your Prometheus selects ServiceMonitors by, e.g. `{release: kube-prometheus-stack}`. |
| monitoring.serviceMonitor.namespace | string | `""` | Namespace to create it in (empty: the release namespace). |
| monitoring.serviceMonitor.relabelings | list | `[]` | Relabelings for the endpoint, as given. |
| monitoring.serviceMonitor.scrapeTimeout | string | `"10s"` | Scrape timeout (Prometheus duration). |
| nameOverride | string | `""` | Override the chart name used in resource names. |
| network.ingresses | list | `[]` | Ingress objects to create, one per entry, named `<release-fullname>-<name>`. Keys: name, className, annotations, hosts (host + paths with path and optional pathType), tls. Set your controller's body-size limit to at least 27 MB (e.g. `nginx.ingress.kubernetes.io/proxy-body-size: 28m`). Example in ci/all-on-values.yaml. |
| network.networkPolicy.enabled | bool | `false` | Ingress-only NetworkPolicy for the webapp pods. Egress stays open: the trust lists, OCSP and timestamp responders are a changing set of public HTTPS hosts; restrict it in your own policy if your CNI resolves names. Needs a CNI that enforces policies. |
| network.networkPolicy.ingressFrom | list | `[]` | NetworkPolicyPeer entries allowed to the traffic port (namespaceSelector, podSelector, ipBlock). Empty: any source, in or outside the cluster (NodePort/LoadBalancer traffic included); list your Ingress controller's namespace to narrow it. |
| network.networkPolicy.managementFrom | list | `[]` | Peers allowed to the management port when the actuator is split: Prometheus, and the `helm test` pod if you still want it to pass (the kubelet probes use the traffic port). Empty: any source. Without the split the actuator sits on the traffic port and ingressFrom governs it; this list is then unused. |
| nodeSelector | object | `{}` | Node selector. |
| podAnnotations | object | `{}` | Extra pod annotations. |
| podLabels | object | `{}` | Extra pod labels. |
| probes.liveness.enabled | bool | `true` | Render the liveness probe. |
| probes.liveness.failureThreshold | int | `3` | Failures before the container is restarted. |
| probes.liveness.initialDelaySeconds | int | `0` | Seconds before the first probe. |
| probes.liveness.path | string | `""` | Path on the traffic port; empty picks the liveness group's path for the layout. |
| probes.liveness.periodSeconds | int | `20` | Seconds between probes. |
| probes.liveness.timeoutSeconds | int | `5` | Seconds before a probe counts as failed. |
| probes.readiness.enabled | bool | `true` | Render the readiness probe. |
| probes.readiness.failureThreshold | int | `3` | Failures before the pod is taken out of the Service. |
| probes.readiness.initialDelaySeconds | int | `0` | Seconds before the first probe. |
| probes.readiness.path | string | `""` | Path on the traffic port; empty picks the readiness group's path for the layout. |
| probes.readiness.periodSeconds | int | `10` | Seconds between probes. |
| probes.readiness.timeoutSeconds | int | `5` | Seconds before a probe counts as failed. |
| probes.startup.enabled | bool | `true` | Render the startup probe. |
| probes.startup.failureThreshold | int | `60` | Failures before the container is restarted; with periodSeconds this is the startup budget for the trust-list download (60 x 10 s = 10 min): tune it to your egress. |
| probes.startup.initialDelaySeconds | int | `10` | Seconds before the first probe. |
| probes.startup.path | string | `""` | Path on the traffic port; empty picks the liveness group's path for the layout. |
| probes.startup.periodSeconds | int | `10` | Seconds between probes. |
| probes.startup.timeoutSeconds | int | `5` | Seconds before a probe counts as failed. |
| replicaCount | int | `1` | Number of webapp replicas (ignored when scaling.hpa is on). Each replica downloads the trust lists itself at startup (see "Known limits"), so scale with that in mind. |
| resources | object | requests 500m / 1Gi, limit 2Gi memory | Container resources. Paketo's memory calculator sizes the JVM heap from the memory LIMIT (it reads the cgroup), so the limit is the knob, not the request. The default handles a 20 MB document with headroom; no CPU limit on purpose (JVM startup and TSL parsing are bursty). |
| runtime.forwardHeadersStrategy | string | `"framework"` | server.forward-headers-strategy: `framework` trusts X-Forwarded-* from the Ingress/edge so the app sees the client address and scheme; `none` when nothing in front sets them. |
| runtime.javaToolOptions | string | `"-XX:+ExitOnOutOfMemoryError"` | JAVA_TOOL_OPTIONS for the JVM; Paketo's memory calculator appends its own flags. ExitOnOutOfMemoryError makes a JVM that ran out of heap exit so Kubernetes restarts it instead of letting it limp along (Paketo's Liberica buildpack sets it too; the duplicate is harmless and the chart value also covers images built another way). |
| runtime.managementPort | int | `8081` | Actuator (health with details, Prometheus, heartbeat, version) on its own port. The traffic Service and the Ingresses never carry it; a ClusterIP Service `<release>-management` exposes it in-cluster for scraping and `helm test`. The traffic port keeps the status-only probe paths /readyz and /livez for the kubelet and external load balancers. Set it equal to service.containerPort to keep the whole actuator on the traffic port (the layout SiVa's docker-compose and system tests expect; the probes then use /monitoring/health/*). |
| runtime.preStopSleepSeconds | int | `5` | Seconds the pod sleeps in preStop before SIGTERM, so endpoint removal has propagated and no new request lands on a pod that is stopping. Runs `sleep` inside the image (present in Paketo images; a distroless image has none). 0 disables the hook. |
| runtime.shutdownTimeout | string | `"30s"` | Graceful shutdown (server.shutdown=graceful): on SIGTERM the server stops accepting new connections and waits up to this long for in-flight validations (spring.lifecycle.timeout-per-shutdown-phase; whole seconds with the `s` unit). Size it to your slowest validation; it applies per shutdown phase, so leave margin below the grace period (the chart refuses to render when sleep + timeout reach terminationGracePeriodSeconds). |
| scaling.hpa.behavior | object | `{}` | HorizontalPodAutoscalerBehavior, as given. |
| scaling.hpa.customMetrics | list | `[]` | Extra MetricSpec entries (custom/external metrics), appended as given. |
| scaling.hpa.enabled | bool | `false` | HorizontalPodAutoscaler (autoscaling/v2). When on, replicaCount is ignored and scaling.pdb applies. Each new pod downloads the trust lists first (minutes), so scale on a sustained signal, not spikes. |
| scaling.hpa.maxReplicas | int | `5` | Upper bound. |
| scaling.hpa.minReplicas | int | `2` | Lower bound; with 1 the PodDisruptionBudget is not rendered. |
| scaling.hpa.targetCPUUtilizationPercentage | int or string | `70` | CPU utilisation target; empty disables the CPU metric. |
| scaling.hpa.targetMemoryUtilizationPercentage | int or string | `""` | Memory utilisation target; empty disables it (the JVM holds its heap, so memory is a poor signal). |
| scaling.pdb.enabled | bool | `true` | PodDisruptionBudget, rendered only when there is more than one pod to budget (replicaCount > 1, or the HPA is on with minReplicas > 1); with one pod it would block every node drain. |
| scaling.pdb.maxUnavailable | int or string | `""` | Set (0, a count or a percentage) to use maxUnavailable instead of minAvailable. |
| scaling.pdb.minAvailable | int or string | `1` | minAvailable (a count or a percentage), used unless maxUnavailable is set. |
| security.automountServiceAccountToken | bool | `false` | Mount the ServiceAccount token. The app never talks to the Kubernetes API. |
| security.podSecurityContext | object | runAsNonRoot, RuntimeDefault seccomp | Pod security context, written to pass Pod Security "restricted" together with securityContext below; overriding a field (or nulling it) relaxes the profile, which only a namespace labelled pod-security.kubernetes.io/enforce=restricted prevents. No runAsUser/runAsGroup: the buildpack image carries a numeric non-root USER (whatever CNB_USER_ID the builder used) and the launcher needs to run as exactly that uid to read /layers; pinning a uid here breaks launch when the image was built as another one. Set them only if your image has no numeric USER. |
| security.securityContext | object | no privilege escalation, drop ALL, read-only root | Container security context. The only writable path the app needs is /tmp (java.io.tmpdir: DSS and DigiDoc4J trust-list caches, temporary policy files; Paketo's certificate loader also copies the JVM truststore there when its layer is read-only) and it is an emptyDir. Logs go to stdout. |
| service.annotations | object | `{}` | Service annotations. |
| service.containerPort | int | `8080` | Container port the app listens on (Paketo process `web`, server.port=8080). |
| service.nodePort | int or string | `""` | Fixed node port (30000-32767); only used when type is NodePort, empty for an allocated one. |
| service.port | int | `8080` | Service port for API traffic. |
| service.type | string | `"ClusterIP"` | ClusterIP, NodePort or LoadBalancer. |
| serviceAccount.annotations | object | `{}` | ServiceAccount annotations. |
| serviceAccount.create | bool | `true` | Create a ServiceAccount for the pods. |
| serviceAccount.name | string | `""` | Name of the ServiceAccount to create or use (empty: the release's full name, or `default` when not created). |
| terminationGracePeriodSeconds | int | `60` | Grace period on pod deletion. Must exceed runtime.preStopSleepSeconds + runtime.shutdownTimeout, or Kubernetes SIGKILLs the JVM while it is still draining. |
| tests.enabled | bool | `true` | Render the `helm test` pod, which GETs /monitoring/health through the management (or traffic) Service. |
| tests.image | string | `"busybox:1.37.0"` | Image for the test pod (needs wget). |
| tests.runAsUser | int | `65534` | uid/gid of the test pod: busybox carries no numeric USER, so runAsNonRoot needs one (65534 = nobody). The pod's restricted context is fixed in its template; this is its only knob. |
| tmpSizeLimit | string | `"1Gi"` | Size limit of the /tmp emptyDir (trust-list caches, a few tens of MB, and temp files); the pod is evicted when it fills up, so keep headroom. Empty for no limit. |
| tolerations | list | `[]` | Tolerations. |
| topologySpreadConstraints | list | one soft constraint on kubernetes.io/hostname | Spread replicas across nodes (soft: scheduling never blocks on it). Each entry is a plain topologySpreadConstraint; labelSelector is filled in by the chart when omitted. Empty list disables. |
| trust.pemConfigMap | string | `""` | Placeholder, not wired yet: a ConfigMap with PEM trust material. SiVa 3.10 ships its TSL and LOTL truststores on the classpath, which is what this chart uses; nothing is mounted. |
| trust.pemSecret | string | `""` | Placeholder, not wired yet: a Secret with PEM trust material. |

## Verifying on a NodePort (kind or any node you can reach)

```sh
helm install siva ./helm/siva-webapp --set image.repository=<your-registry>/siva-webapp \
  --set service.type=NodePort --set service.nodePort=30080
kubectl rollout status deployment/siva-siva-webapp --timeout=15m   # trust lists download first
curl -X POST -H 'Content-Type: application/json' \
     --data @build-helpers/sample-requests/<any>.json http://<node>:30080/validate
helm test siva
```

On kind, `<node>` is the control-plane container (`<cluster>-control-plane`) from the kind
network, or `localhost` if the cluster was created with an `extraPortMappings` entry for 30080.

## Known limits

- Readiness waits for the mandatory trusted lists (`siva.tsl.loader.mandatoryTerritories`, `EE`
  by default); every other territory can be missing or stale without taking the pod out of
  rotation, and health only says so in the `tsl` component's details. Alerting is left to you.
- Every replica fetches the trust lists itself; replicas started at different times can hold
  different trust states until the next refresh.
- Metrics are at `<release>-management:8081/monitoring/prometheus` (the service port when
  `runtime.managementPort` equals `service.containerPort`); `monitoring.serviceMonitor` scrapes
  it with the Prometheus operator, otherwise scrape it yourself. The alert rules cover what the
  application publishes today; trust-list freshness and OCSP/TSA failures have no metric yet.
- Health with details, heartbeat, version and metrics live on the management port (`8081` by
  default) and are reachable only inside the cluster through `<release>-management`; the
  traffic port answers 404 under `/monitoring` and serves only the status-only `/readyz` and
  `/livez`, which the kubelet probes and an external load balancer's health check use. SiVa's
  own system tests expect everything on the traffic port, so a test instance sets
  `runtime.managementPort` to the traffic port (see `values-kind.yaml`); the probes then use
  `/monitoring/health/readiness` and `/liveness`.
- Graceful shutdown needs `sleep` in the image for the preStop pause (Paketo images have it;
  set `runtime.preStopSleepSeconds: 0` otherwise) and a `terminationGracePeriodSeconds` above
  sleep + `runtime.shutdownTimeout`; the chart refuses to render otherwise.
- Put JVM flags in `runtime.javaToolOptions`, not in `env`: an `env` entry named
  `JAVA_TOOL_OPTIONS` replaces the chart's.
- Trust material is the classpath default; rotating it means a new image.
