# siva-webapp

![Version: 0.10.0](https://img.shields.io/badge/Version-0.10.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 3.11.0-SNAPSHOT](https://img.shields.io/badge/AppVersion-3.11.0--SNAPSHOT-informational?style=flat-square)

Reference deployment of [SiVa](https://github.com/open-eid/SiVa), the Estonian digital-signature
validation service, on Kubernetes. Like the project's docker-compose file it is a reference, not a
product: best-effort, no support promise, build and run it yourself.

Licence: EUPL 1.1, the same as the SiVa repository this chart belongs with.

## Before you install

- **Build your own image.** SiVa publishes source only. `./mvnw spring-boot:build-image` in the
  SiVa checkout produces a Paketo image of `siva-webapp`; push it to a registry your cluster can
  pull from and set `image.repository` / `image.tag`.
- **Egress.** In the default `tsl.mode: online` each pod downloads the EU LOTL and the
  member-state trust lists at startup and on a nightly cron; in snapshot mode only the fetcher
  does (see "Trust lists"). Allow HTTPS egress to those hosts from whichever pods download, and
  from every pod to the OCSP/TSA responders of the certificates you validate.
- **Trust material.** This iteration uses the truststores shipped on the application classpath.
  Supplying your own is not wired yet (`trust.*` is a placeholder).

```sh
helm install siva ./helm/siva-webapp \
  --set image.repository=registry.example.invalid/siva-webapp --set-string image.tag=3.10.3
```

`image.tag` and `env[].value` are strings; use `--set-string` for numeric-looking values (a git
short sha such as `1234567` would otherwise be parsed as a number and rejected by the values
schema).

## Trust lists

SiVa validates against the EU list of trusted lists and the member-state lists it points to.
The chart offers two ways to get them into the pods:

- `tsl.mode: online` (default): every pod downloads them itself at startup and on SiVa's
  nightly cron. Simple, needs egress from every pod, takes minutes before a pod is ready, and
  replicas started at different times can hold different trust states for a while.
- `tsl.mode: snapshot`: a CronJob (`<release>-tsl-fetch`) runs the same image in SiVa's
  `tsl-fetch` profile, downloads the lists into a working cache and publishes them as a
  snapshot (`snapshots/<id>/` plus `current`) into a shared volume; the webapp pods mount it
  read-only, load the snapshot `current` names and poll for a new one
  (`tsl.snapshot.pollInterval`). A fresh pod is ready in seconds, every replica validates
  against the same anchors, and the webapp needs no egress to the list hosts. The fetcher
  publishes only when the mandatory territories (`siva.tsl.loader.mandatoryTerritories`) came
  through, so `current` always names the last good snapshot; a list whose download fails keeps
  its last copy in the working cache. A Job runs one fetch at install (and whenever the fetcher
  pod changes), so the pods have something to load before the first schedule. Needs a SiVa
  build with the fetcher (3.11.0 and later).

Snapshot mode needs a store the fetcher can write and every webapp pod can read: a
ReadWriteMany claim you bring (`tsl.snapshot.volume`) or one the chart creates
(`tsl.snapshot.persistentVolumeClaim.create`, ReadWriteOnce is enough on a single node). The
fetcher renames files and directories in it, so the filesystem must support rename; an
object-store mount without rename (mountpoint-s3) cannot hold it. The fetcher gets the same
ConfigMap, `env` and secret as the webapp, so its snapshot matches what the webapp expects
(same profile, same `trustedTerritories`); `tsl.fetcher.env` adds fetcher-only variables.
`monitoring.prometheusRule` gains `SivaTslFetchFailing` (no successful fetch for
`tsl.fetcher.staleAfterSeconds`); mind that a snapshot that goes stale degrades every replica
at once, which is why the schedule should sit well inside the lists' validity.

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
| replicaCount | int | `1` | Number of webapp replicas (ignored when scaling.hpa is on). In `tsl.mode: online` each replica downloads the trust lists itself at startup (minutes); in snapshot mode it reads the published snapshot (seconds). |
| resources | object | requests 500m / 1Gi, limit 2Gi memory | Container resources. Paketo's memory calculator sizes the JVM heap from the memory LIMIT (it reads the cgroup), so the limit is the knob, not the request. The default handles a 20 MB document with headroom; no CPU limit on purpose (JVM startup and TSL parsing are bursty). |
| runtime.forwardHeadersStrategy | string | `"framework"` | server.forward-headers-strategy: `framework` trusts X-Forwarded-* from the Ingress/edge so the app sees the client address and scheme; `none` when nothing in front sets them. |
| runtime.javaToolOptions | string | `"-XX:+ExitOnOutOfMemoryError"` | JAVA_TOOL_OPTIONS for the JVM; Paketo's memory calculator appends its own flags. ExitOnOutOfMemoryError makes a JVM that ran out of heap exit so Kubernetes restarts it instead of letting it limp along (Paketo's Liberica buildpack sets it too; the duplicate is harmless and the chart value also covers images built another way). |
| runtime.managementPort | int | `8081` | Actuator (health with details, Prometheus, heartbeat, version) on its own port. The traffic Service and the Ingresses never carry it; a ClusterIP Service `<release>-management` exposes it in-cluster for scraping and `helm test`. The traffic port keeps the status-only probe paths /readyz and /livez for the kubelet and external load balancers. Set it equal to service.containerPort to keep the whole actuator on the traffic port (the layout SiVa's docker-compose and system tests expect; the probes then use /monitoring/health/*). |
| runtime.preStopSleepSeconds | int | `5` | Seconds the pod sleeps in preStop before SIGTERM, so endpoint removal has propagated and no new request lands on a pod that is stopping. On Kubernetes 1.30+ this is the kubelet's own Sleep action and needs nothing in the image; on older clusters it execs `sleep`, which Paketo's default tiny run image does not have (the hook then fails and the pause is lost). 0 disables the hook. |
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
| security.podSecurityContext | object | runAsNonRoot, RuntimeDefault seccomp | Pod security context, written to pass Pod Security "restricted" together with securityContext below; overriding a field (or nulling it) relaxes the profile, which only a namespace labelled pod-security.kubernetes.io/enforce=restricted prevents. No runAsUser/runAsGroup: the buildpack image carries a numeric non-root USER (whatever CNB_USER_ID the builder used) and the launcher needs to run as exactly that uid to read /layers; pinning a uid here breaks launch when the image was built as another one. Set them only if your image has no numeric USER. Applies to the fetcher pod too; add `fsGroup` when your storage does not make the snapshot volume writable for the image's user. |
| security.securityContext | object | no privilege escalation, drop ALL, read-only root | Container security context. The only writable path the app needs is /tmp (java.io.tmpdir: DSS and DigiDoc4J trust-list caches in online mode, temporary policy files; Paketo's certificate loader also copies the JVM truststore there when its layer is read-only) and it is an emptyDir; in snapshot mode the fetcher also writes the store. Logs go to stdout. |
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
| tsl.fetcher.activeDeadlineSeconds | int | `1800` | Seconds a fetch may run before it is killed (30 lists take a few minutes). |
| tsl.fetcher.backoffLimit | int | `2` | Retries of a failed fetch within one schedule slot (a new pod each time). |
| tsl.fetcher.bootstrapJob | bool | `true` | Also run one fetch as a Job at install and whenever the fetcher pod changes (image, config), so a fresh install has a snapshot without waiting for the schedule. Named after a hash of the pod spec; the finished Job stays until the next change replaces it. |
| tsl.fetcher.env | list | `[]` | Extra environment variables for the fetcher only; it already gets `env`, `envFrom`, the ConfigMap and `runtime.javaToolOptions` (same profile and settings as the webapp, which is what makes its snapshot the one the webapp expects). |
| tsl.fetcher.failedJobsHistoryLimit | int | `3` | Failed fetch Jobs kept. |
| tsl.fetcher.keepSnapshots | int | `3` | Snapshots kept besides the current one (siva.tsl.fetcher.keepSnapshots); a pod in the middle of loading an older one is not pulled away. |
| tsl.fetcher.resources | object | requests 250m / 512Mi, limit 1Gi memory | Resources of the fetcher pod; the JVM is sized from the memory limit as for the webapp. |
| tsl.fetcher.schedule | string | `"0 */6 * * *"` | Cron schedule of the fetch. Keep it well inside the shortest list validity you rely on: a snapshot that goes stale degrades every replica at once, not one by one. |
| tsl.fetcher.staleAfterSeconds | int | `86400` | Seconds without a successful fetch before the SivaTslFetchFailing alert fires (monitoring.prometheusRule, needs kube-state-metrics). |
| tsl.fetcher.startingDeadlineSeconds | string | `""` | Seconds after a missed schedule the fetch may still start; empty for the default. |
| tsl.fetcher.successfulJobsHistoryLimit | int | `3` | Finished fetch Jobs kept for `kubectl get jobs` and the alert rule. |
| tsl.fetcher.timeZone | string | `""` | IANA time zone of the schedule (Kubernetes 1.27+); empty for the controller's. |
| tsl.fetcher.workVolume | object | `{}` | Working cache the fetcher downloads into, kept between runs so that a list whose download fails keeps its last copy: a volume source as above, or empty for a `work/` directory inside the store. |
| tsl.mode | string | `"online"` | `online`: every pod downloads the lists itself at startup and on SiVa's nightly cron (needs HTTPS egress to ~30 hosts, minutes until ready, replicas started at different times can hold different trust states). `snapshot`: a CronJob running this image in SiVa's `tsl-fetch` profile downloads them, publishes a snapshot into a shared volume and every pod reads that (ready in seconds, identical trust state, no egress to the list hosts from the webapp); the fetcher publishes only when the mandatory territories came through, so `current` always names the last good snapshot. Needs a SiVa build with the fetcher (3.11.0+). |
| tsl.snapshot.mountPath | string | `"/var/siva/tsl"` | Where the snapshot store is mounted (read-only in the webapp pods, read-write in the fetcher). |
| tsl.snapshot.persistentVolumeClaim.accessModes | list | `["ReadWriteMany"]` | Access modes of the claim. |
| tsl.snapshot.persistentVolumeClaim.annotations | object | `{}` | Claim annotations. |
| tsl.snapshot.persistentVolumeClaim.create | bool | `false` | Create the claim (`<release>-tsl`, kept on uninstall). ReadWriteMany when the fetcher and the webapp pods may land on different nodes; ReadWriteOnce is enough on a single node (kind). |
| tsl.snapshot.persistentVolumeClaim.size | string | `"1Gi"` | Requested size; a snapshot is about 10 MB, keepSnapshots + 1 of them are kept. |
| tsl.snapshot.persistentVolumeClaim.storageClassName | string | `""` | StorageClass; empty for the cluster default. |
| tsl.snapshot.pollInterval | string | `"PT1M"` | How often a pod checks `current` for a new snapshot (ISO-8601 duration; siva.tsl.loader.snapshotPollInterval). Replicas converge within this of a publish; the nightly refresh stays as the safety net. |
| tsl.snapshot.volume | object | `{}` | The store: a volume source without the `name`, for example `persistentVolumeClaim: {claimName: siva-tsl}` for a ReadWriteMany claim you created, or a CSI bucket mount. Ignored when persistentVolumeClaim.create is on. The fetcher renames files and directories in it, so the filesystem must support rename; an object-store mount that does not (mountpoint-s3) cannot hold it. |

## Verifying on a NodePort (kind or any node you can reach)

```sh
helm install siva ./helm/siva-webapp --set image.repository=<your-registry>/siva-webapp \
  --set service.type=NodePort --set service.nodePort=30080
kubectl rollout status deployment/siva-siva-webapp --timeout=15m   # online mode downloads the trust lists first
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
- In online mode every replica fetches the trust lists itself; replicas started at different
  times can hold different trust states until the next refresh. Snapshot mode (see "Trust
  lists") removes that, at the price of a shared volume and a CronJob.
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
- The preStop pause uses the kubelet's Sleep action on Kubernetes 1.30+ and needs nothing in
  the image. Below 1.30 it execs `sleep`, which Paketo's default tiny run image does not ship:
  there, use a run image that has it or set `runtime.preStopSleepSeconds: 0` (and accept that a
  rollout may refuse a request or two). `terminationGracePeriodSeconds` must exceed sleep +
  `runtime.shutdownTimeout`; the chart refuses to render otherwise.
- Put JVM flags in `runtime.javaToolOptions`, not in `env`: an `env` entry named
  `JAVA_TOOL_OPTIONS` replaces the chart's.
- Trust material is the classpath default; rotating it means a new image.
