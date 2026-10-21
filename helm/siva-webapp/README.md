# siva-webapp

![Version: 3.11.0-SNAPSHOT](https://img.shields.io/badge/Version-3.11.0--SNAPSHOT-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 3.11.0-SNAPSHOT](https://img.shields.io/badge/AppVersion-3.11.0--SNAPSHOT-informational?style=flat-square)

Reference deployment of [SiVa](https://github.com/open-eid/SiVa), the Estonian digital-signature
validation service, on Kubernetes. Like the project's docker-compose file it is a reference, not a
product: best-effort, no support promise, build and run it yourself.

Licence: EUPL 1.1, the same as the SiVa repository this chart belongs with.

Pods are disposable. In snapshot mode (`tsl.mode: snapshot`, see "Trust lists") a new pod loads
the EU trust lists from a shared store and is ready in seconds with no download of its own, so
the deployment is friendly to the descheduler, to daily rolling restarts, to autoscaling and to
preemptible nodes; every replica validates against the same anchors. The pods carry the
safe-to-evict annotations the descheduler and the cluster autoscaler look for (`evictable`),
and the PodDisruptionBudget bounds how many go at once; which descheduler, and on what policy,
is yours to choose. Object storage as the store,
bridged by a sync sidecar rather than a CSI driver, is the next step on that path.

## Before you install

- **Build your own image.** SiVa publishes source only. `./mvnw spring-boot:build-image` in the
  SiVa checkout produces a Paketo image of `siva-webapp`; push it to a registry your cluster can
  pull from and set `image.repository` / `image.tag`.
- **Egress.** In the default `tsl.mode: online` each pod downloads the EU LOTL and the
  member-state trust lists at startup and on a nightly cron; in snapshot mode only the fetcher
  does (see "Trust lists"). Allow HTTPS egress to those hosts from whichever pods download, and
  from every pod to the OCSP/TSA responders of the certificates you validate.
- **Trust material.** By default the pods use the truststores shipped on the application
  classpath (the CAs of the trust-list hosts, the LOTL signers), and rotating them means a new
  image. `trust.*` mounts the same material as PEM files instead and rotates them in place; see
  "Trust material".

```sh
helm install siva ./helm/siva-webapp \
  --set image.repository=registry.example.invalid/siva-webapp --set-string image.tag=3.10.3
```

`image.tag` and `env[].value` are strings; use `--set-string` for numeric-looking values (a git
short sha such as `1234567` would otherwise be parsed as a number and rejected by the values
schema).

## Configuration

`config.spring` is a Spring Boot configuration override in application.yml syntax, rendered as
the ConfigMap file `application.yml` and named to SiVa through
`SPRING_CONFIG_ADDITIONAL_LOCATION`, as the project's docker-compose setup does. It is a map, so
one key can be set from the command line and two overlays merge key by key:

```sh
helm upgrade siva ./helm/siva-webapp --reuse-values \
  --set config.spring.siva.tsl.loader.trusted-territories='{EE,LV,LT}'
```

```yaml
config:
  spring:
    siva:
      tsl:
        loader:
          trusted-territories: [EE, LV, LT]
    logging:
      level:
        ee.openeid: DEBUG
```

Write keys nested, in the kebab-case Spring Boot documents (`max-request-size-limit`); a dotted
key on one line survives `toYaml` but not `--set`, and Spring's relaxed binding accepts the
camelCase spelling of the SiVa deployment guide as well. Quote values that YAML would read as a
number or boolean when the property is a string. The values schema (`values.schema.json`)
checks every key against the configuration metadata the SiVa build emits (SiVa's own
`siva.*` and `t-level-signature-filter.*`, and Spring Boot's `server`, `management`, `logging`
and `spring`), so a mistyped key fails `helm lint`, `template`, `install` and `upgrade` naming
the key; a prefix nothing declares (`dss.*`) passes. The fragment is generated from the SiVa
build and committed, so an upgrade of SiVa regenerates it. Secrets never go into `config.spring`: a ConfigMap is not a Secret. Reference an
environment variable from `envFrom.existingSecret` instead
(`siva.signature-service.pkcs12.password: ${SIVA_PKCS12_PASSWORD}`). The chart's own settings
(`runtime.*`, `tsl.*`, `trust.*`) are injected as environment variables, which win over the file,
so an override here cannot drop them.

`config.files` renders further files into the same ConfigMap, mounted read-only beside
`application.yml` at `config.mountPath`; a change to either rolls the pods through the
`checksum/config` annotation. The two the docker-compose setup mounts:

```yaml
config:
  spring:
    logging:
      config: /etc/siva-webapp/logback.xml
    siva:
      bdoc:
        digidoc4j-configuration-file: file:/etc/siva-webapp/siva-digidoc4j.yaml
  files:
    logback.xml: |
      <configuration>
        <include resource="org/springframework/boot/logging/logback/base.xml"/>
        ...
      </configuration>
    siva-digidoc4j.yaml: |
      # the whole digidoc4j configuration: it replaces the bundled siva-digidoc4j.yaml
      ...
```

`extraVolumes`, `extraVolumeMounts` and `extraContainers` pass pod spec through for what the
chart does not model (a sidecar, a mounted Secret the container reads itself), on the webapp
Deployment only. The pods run under the Pod Security "restricted" profile, so an extra container
needs its own `securityContext` (no privilege escalation, all capabilities dropped, read-only
root filesystem); with `network.networkPolicy.egress` on it also needs its own
`additionalRules`. The example under "Trust lists" is a sync sidecar for a snapshot store in a
bucket.

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
  its last copy in the working cache, and a run that had to use one still publishes but fails
  its Job (see "Fetch Job outcomes" below). A Job runs one fetch at install (and whenever the
  fetcher pod changes), so the pods have something to load before the first schedule. Needs a
  SiVa build with the fetcher (3.11.0 and later).

Snapshot mode needs a store the fetcher can write and every webapp pod can read: a
ReadWriteMany claim you bring (`tsl.snapshot.volume`) or one the chart creates
(`tsl.snapshot.persistentVolumeClaim.create`, ReadWriteOnce is enough on a single node). The
fetcher renames files and directories in it, so the filesystem must support rename; an
object-store mount without rename (mountpoint-s3) cannot hold it. The fetcher gets the same
ConfigMap, `env` and secret as the webapp, so its snapshot matches what the webapp expects
(same profile, same territories); `tsl.fetcher.env` adds fetcher-only variables. Set the
countries in `tsl.territories` so both get them.

Clusters without ReadWriteMany storage but with object storage (S3, Azure Blob) bridge the
store outside SiVa, which has no object-storage client by design: a sync tool in its own
container copies the published snapshot to the bucket on the fetcher side and, in each webapp
pod, a sidecar syncs the bucket into a local directory the pod reads. The webapp side is two
values, the store as a pod-local volume and the sidecar through `extraContainers`; `rclone` is
one tool that speaks every such API, and the deployer updates it on their own schedule:

```yaml
tsl:
  mode: snapshot
  snapshot:
    volume:
      emptyDir: {}
extraContainers:
  - name: tsl-sync
    image: rclone/rclone:1.68.2
    args: ["sync", "--interval", "5m", "siva-bucket:siva-tsl", "/var/siva/tsl"]
    envFrom:
      - secretRef:
          name: siva-tsl-sync   # RCLONE_CONFIG_SIVA-BUCKET_* or the cloud's workload identity
    volumeMounts:
      - name: tsl-store         # the chart's store volume, read-write for the sidecar
        mountPath: /var/siva/tsl
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop: [ALL]
    resources:
      requests: {cpu: 10m, memory: 32Mi}
      limits: {memory: 64Mi}
```

The fetcher side (uploading a published snapshot to the bucket) has no hook in the chart yet,
and a sync has no rename atomicity: `current` can arrive before the last file of the snapshot it
names, and SiVa does not yet refuse an incomplete snapshot, so order the upload so that
`snapshots/<id>/` is complete before `current` is written. Freshness on a pod is then the sync
interval plus `tsl.snapshot.pollInterval`.

`tsl.fetcher.period` (six hours by default, at most a week) is the one setting the rest follows:
the CronJob schedule and `tsl.snapshot.maxAge` (max(4 x period, 48 h)) derive from it, and the
fetcher writes it into every snapshot, from where the pods publish it as
`siva_tsl_refresh_period_seconds`; the trust-list alerts take their thresholds from that gauge
(two periods plus `monitoring.prometheusRule.tsl.grace` for a stale snapshot or a list served
from its last copy, two periods plus the lead time before a list expires), so the rules carry
no period of their own and hold for a plain-cron deployment that sets `siva.tsl.fetcher.period`
itself. A snapshot that goes stale degrades every replica at once, and member-state lists run
out within three to six months of issue, which is why a period beyond a week is refused. The
deployment guide's "Choosing the fetch period" has the table.

### Fetch Job outcomes

The fetcher's exit code is the Job's result, and the Job is what the cluster's own alerts
watch (kube-prometheus-stack's `KubeJobFailed`), so the chart adds no fetch alert of its own:

| Exit | Meaning | Job |
|---|---|---|
| 0 | Every list downloaded, snapshot published | Complete |
| 1 | A mandatory territory's list is missing, expired or badly signed; nothing published, `current` unchanged | Failed at once |
| 2 | The store could not be written | Failed after `tsl.fetcher.backoffLimit` retries |
| 3 | A list's download failed and the run published its last good copy (the fetch log's `using the last copy on disk` line names the list and the reason) | Failed at once |

Exit 1 and 3 fail for reasons a retry seconds later cannot change, so a `podFailurePolicy`
fails the Job without the usual retries (Kubernetes 1.26 and later, the chart's floor). After
exit 3 the pods keep validating on good lists and `SivaTslListNotDownloaded` says which list
has not been downloaded for two periods; the next successful run clears it. `KubeJobFailed`
stays on until the Failed Job object is gone: the history limits rotate failed Jobs out only
as newer ones fail, so after an outage delete the Job once you have read its log, or set
`tsl.fetcher.ttlSecondsAfterFinished` to have them expire (then the log goes with them). The
bootstrap Job gets no TTL: it is a tracked resource, and a GitOps controller would recreate
and re-run it.

Whoever can write the store can point every pod at any snapshot whose lists are still inside
their validity window, so treat write access to it as trust-list authority: only the fetcher
writes, the webapp mounts it read-only, and `tsl.snapshot.maxAge` makes a pod refuse a
snapshot older than that rather than serve it. The fetcher pod gets its own
Secret (`tsl.fetcher.existingSecret`), never the webapp's: it parses thirty documents from the
internet and should hold nothing else. With `network.networkPolicy.egress` on, the webapp pods
reach only DNS and the public internet on 80 and 443 (OCSP and timestamp responders) and the
fetcher pods the same with no ingress at all. The store holds nothing worth backing up: a lost
store is rebuilt by the next fetch, so leave the claim out of your backup scope.

## Trust material

Two sets of certificates decide what the pods trust before any signature is validated: the CAs
of the hosts the trust lists are downloaded from (TLS), and the certificates the LOTL is signed
with (announced in the Official Journal). SiVa ships both as PKCS12 files on its classpath.
`trust.*` replaces them with PEM files mounted at `trust.mountPath`: `trust.pem` renders the
files into the ConfigMap `<release>-trust`, `trust.existingConfigMap` or `trust.existingSecret`
takes an object something else maintains (a trust-manager `Bundle` with a ConfigMap or Secret
target writes exactly this shape: one concatenated PEM per key). The chart wires each file as a
Spring SSL bundle with `reload-on-update` and names it to SiVa (`siva.tsl.loader.sslBundle`,
`lotlBundle`; SiVa 3.11.0 and later), for the webapp and the fetcher alike. A changed file
reaches the pods within the kubelet's sync period, Spring Boot reloads the bundle and logs it,
and the trust lists use it from the next download or refresh: no restart, no image.

The CA file is the whole trust for the trust-list downloads, as the classpath truststore is
today, so it must hold every CA those hosts chain to. It does not touch the JVM's own
truststore, which the OCSP, AIA and timestamp clients use; `trust.caCertificatesBinding`
hands the same file to Paketo's ca-certificates helper, which adds it to the JVM truststore at
launch (Paketo images only, pod restart on change).

## Versioning

The chart has no releases of its own: `version` and `appVersion` are the SiVa version (the Maven
project version, `-SNAPSHOT` included) and change only in SiVa's own version-bump commit. The
chart from a SiVa checkout is therefore the chart for that SiVa, and `image.tag` left empty
runs that version.

Lab and other internal builds get a build id, generated at build time and never committed:
`<SiVa version without its pre-release>-dev.<UTC yyyymmddTHHMMSS>.g<short sha>`, for example
`3.11.0-dev.20261019T084942.g597535b`. It is both the image tag and the version of the chart
package pushed with it, so one build is one identifier everywhere. `dev` is constant, the
fixed-width timestamp is a single alphanumeric identifier that semver compares lexically, so
later builds sort higher (`T` keeps it alphanumeric: a numeric identifier with a leading zero
would be invalid), and the sha is for tracing. The `image.tag` in `values-kind.yaml` is the
placeholder `set-by-chart-push` that the build tooling stamps into its copy of the chart;
the chart refuses to render the placeholder, so installing that overlay by hand needs
`--set-string image.tag=<tag>`. A tool that tracks "the newest version" must admit pre-releases
(a semver constraint such as `>=0.0.0-0`; a bare `*` matches release versions only).

## Values

Generated from the comments in `values.yaml` by helm-docs (1.14; run `helm-docs` in this
directory); edit the comments, not this table.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` | Affinity rules, as given. |
| config.files | object | `{}` | Further files rendered into the same ConfigMap and mounted read-only beside application.yml at mountPath, by file name (no directories; `application.yml` is reserved). Point SiVa at them through config.spring: a Logback configuration as `logging.config: /etc/siva-webapp/logback.xml`, a digidoc4j override as `siva.bdoc.digidoc4j-configuration-file: file:/etc/siva-webapp/siva-digidoc4j.yaml` (the whole file, replacing the bundled one). Like config.spring, a change rolls the pods (checksum annotation). Public configuration only, this is a ConfigMap. |
| config.mountPath | string | `"/etc/siva-webapp"` | Where the Spring Boot configuration override is mounted; the container gets SPRING_CONFIG_ADDITIONAL_LOCATION=file:<mountPath>/application.yml. |
| config.spring | object | `{}` | Spring Boot configuration override in application.yml syntax, as a map: rendered with toYaml into the ConfigMap `application.yml` (the mechanism of the project's docker-compose.yaml). Properties here win over the application.yml on the classpath; the `siva.*` keys are listed in the SiVa deployment guide (docs/siva3/deployment_guide.md), in their canonical kebab-case. A map, so `--set config.spring.siva.http.request.max-request-size-limit=27MB` works and two overlays merge key by key; write keys nested, never dotted on one line, and quote strings that look like numbers or booleans ("08", "yes"). The values schema checks the keys SiVa and Spring Boot declare (see the chart README, "Spring configuration"); unknown prefixes pass. Secrets never go here: reference an environment variable from envFrom.existingSecret instead (`password: ${SIVA_PKCS12_PASSWORD}`). Keep the chart's runtime.* and tsl.* settings out of it, they are injected as environment variables so that an override here cannot drop them. Examples of what belongs in a private overlay: `siva: {tsl: {loader: {trusted-territories: [EE, LV, LT]}}}`. |
| env | list | `[]` | Extra environment variables for the container (plain Kubernetes EnvVar entries). Spring relaxed binding applies, e.g. `{name: SIVA_TSL_LOADER_TRUSTEDTERRITORIES, value: "EE,LV"}` (values are strings: use `--set-string` on the command line). Do not set JAVA_TOOL_OPTIONS or the runtime.* variables here: a later duplicate wins and silently replaces the chart's value; use runtime.* instead. |
| envFrom.existingSecret | string | `""` | Name of an existing Secret whose keys become environment variables (passwords, keystore passphrases). Not created by the chart. |
| evictable | bool | `true` | Mark the webapp pods as safe to evict: the descheduler and the cluster autoscaler both skip pods with local storage by default, and /tmp is an emptyDir, so without the annotations `cluster-autoscaler.kubernetes.io/safe-to-evict` and `descheduler.alpha.kubernetes.io/evict` neither would ever move a SiVa pod. Nothing in a pod is worth keeping (the trust-list cache is re-downloaded or re-read from the store); the PodDisruptionBudget still bounds how many pods go at once and graceful shutdown drains the one that goes. The fetcher pods are not marked: a Job interrupted mid-run just retries. |
| extraContainers | list | `[]` | Extra containers of the webapp pods (Container entries), rendered after siva-webapp. |
| extraVolumeMounts | list | `[]` | Extra mounts of the siva-webapp container (VolumeMount entries), of volumes from extraVolumes or the chart's own (`config`, `tmp`, `tsl-store`, `trust`). |
| extraVolumes | list | `[]` | Extra volumes of the webapp pods (Volume entries). |
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
| monitoring.prometheusRule.enabled | bool | `false` | Alert rules (PrometheusRule CRD): SivaValidationErrorRate and SivaOomKilled from Spring Boot's http_server_requests metrics and kube-state-metrics (pod readiness, crash loops and failed fetch Jobs are left to the cluster's own rules, e.g. kube-prometheus-stack's); the trust-list rules (a configured territory without anchors, a list close to its NextUpdate or served from its last copy, in snapshot mode a stale snapshot) from SiVa's siva_tsl_* gauges, with the refresh period read from siva_tsl_refresh_period_seconds rather than rendered in. |
| monitoring.prometheusRule.labels | object | `{}` | Labels your Prometheus selects PrometheusRules by. |
| monitoring.prometheusRule.namespace | string | `""` | Namespace to create it in (empty: the release namespace). |
| monitoring.prometheusRule.pendingRules | bool | `false` | Rules for signals the application does not publish yet: oversized requests (answered 400 today, no 413) and OCSP/TSA failures. Off until it does. |
| monitoring.prometheusRule.tsl.grace | string | `"PT1H"` | Added to two refresh periods before SivaTslSnapshotStale and SivaTslListNotDownloaded fire (ISO-8601). Add the interval of any sync that copies the store to the pods. |
| monitoring.prometheusRule.tsl.leadTime | string | `"P7D"` | Time you give yourself to react before a list expires: SivaTslListExpiringSoon fires at two refresh periods plus this before its NextUpdate (ISO-8601). |
| monitoring.serviceMonitor.applicationLabel | bool | `true` | Add an `application` label (the release's full name) to every scraped series: the label Micrometer's community Grafana dashboards select by (see the README's "Dashboards and alerts that pair with SiVa"). Turn it off when SiVa's own config sets `management.metrics.tags.application`. |
| monitoring.serviceMonitor.enabled | bool | `false` | Prometheus-operator ServiceMonitor scraping /monitoring/prometheus on the management Service (the traffic Service when the actuator is not split). Needs the CRD. |
| monitoring.serviceMonitor.interval | string | `"30s"` | Scrape interval (Prometheus duration). |
| monitoring.serviceMonitor.labels | object | `{}` | Labels your Prometheus selects ServiceMonitors by, e.g. `{release: kube-prometheus-stack}`. |
| monitoring.serviceMonitor.namespace | string | `""` | Namespace to create it in (empty: the release namespace). |
| monitoring.serviceMonitor.relabelings | list | `[]` | Relabelings for the endpoint, as given (after the application label). |
| monitoring.serviceMonitor.scrapeTimeout | string | `"10s"` | Scrape timeout (Prometheus duration). |
| nameOverride | string | `""` | Override the chart name used in resource names. |
| network.ingresses | list | `[]` | Ingress objects to create, one per entry, named `<release-fullname>-<name>`. Keys: name, className, annotations, hosts (host + paths with path and optional pathType), tls. Set your controller's body-size limit to at least 27 MB (e.g. `nginx.ingress.kubernetes.io/proxy-body-size: 28m`). Example in ci/all-on-values.yaml. |
| network.networkPolicy.egress.additionalRules | list | `[]` | Extra NetworkPolicyEgressRule entries appended as given (for example a private OCSP responder or an egress proxy). |
| network.networkPolicy.egress.dns | bool | `true` | Allow DNS (UDP and TCP 53) to any destination. |
| network.networkPolicy.egress.enabled | bool | `false` | Also restrict egress. The webapp then reaches only DNS and `to` on `ports` (OCSP and timestamp responders, and in online mode the trust-list hosts: public HTTPS and, for some OCSP responders, HTTP); the fetcher pods get the same rules in their own policy (`<release>-tsl-fetch`, no ingress at all). The default `to` is the whole internet minus the private ranges and the cloud metadata address, so a compromised pod cannot reach cluster services or the node; tighten it to your responders' addresses if your CNI cannot resolve names. |
| network.networkPolicy.egress.ports | list | `[80,443]` | Ports the pods may reach on `to`. |
| network.networkPolicy.egress.to | list | 0.0.0.0/0 except 10/8, 172.16/12, 192.168/16, 169.254.169.254/32 | NetworkPolicyPeer entries the pods may reach on `ports`. |
| network.networkPolicy.enabled | bool | `false` | NetworkPolicy for the webapp pods: ingress to the two ports from the peers below and, with egress.enabled, egress limited to DNS and the public internet. Needs a CNI that enforces policies. |
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
| runtime.httpKeepAlive | bool | `false` | HTTP keep-alive on the traffic port. Off sets server.tomcat.max-keep-alive-requests=1: every response says `Connection: close`, so no client or ingress holds an idle connection to a pod. On, SIGTERM pauses Tomcat while the pod's idle keep-alive connections are still open and a request already on its way over one of them meets a closed socket instead of an answer (one lost request per idle pooled connection per pod stop, measured on kind; the preStop sleep covers new connections only). A validation costs far more than a TCP handshake. |
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
| trust.caCertificatesBinding | bool | `false` | Also hand `tslCa` to the JVM's own truststore through Paketo's ca-certificates binding (`/platform/bindings/ca-certificates`), so the OCSP, AIA and timestamp clients trust those CAs on top of the image's. Read once at launch: a changed `pem` rolls the pods (checksum annotation), a changed existing object needs a restart. Paketo images only; an image built another way ignores the binding. |
| trust.existingConfigMap | string | `""` | An existing ConfigMap with the PEM files, instead of `pem`. |
| trust.existingSecret | string | `""` | An existing Secret with the PEM files, instead of `pem` (a Secret for public certificates is a convention of the tools that write them, not a secret). |
| trust.lotlSigners | string | `"lotl-signers.pem"` | File with the LOTL signer certificates (the ones the Official Journal announces), rendered as the SSL bundle `lotl` and siva.tsl.loader.lotlBundle=lotl; read again before every refresh. Empty: the classpath truststore. |
| trust.mountPath | string | `"/etc/siva/trust"` | Where the files are mounted, read-only, in the webapp and fetcher pods. |
| trust.pem | object | `{}` | PEM files by name, rendered into the ConfigMap `<release>-trust`: public certificates only, one concatenated PEM per purpose, under the names `tslCa` and `lotlSigners` give. For material managed outside the chart (a trust-manager Bundle, a Secret written by another tool) leave this empty and name the object in existingConfigMap or existingSecret; it must hold the same file names. |
| trust.tslCa | string | `"tsl-ca.pem"` | File with the CA certificates the trust-list downloads trust (TLS to the LOTL and member-state list hosts), rendered as the Spring SSL bundle `tsl` and siva.tsl.loader.sslBundle=tsl. Like SiVa's classpath truststore it is the whole trust for those downloads, not an addition to the JVM's, so it must hold every CA the list hosts chain to. Empty: the downloads keep the classpath truststore. |
| tsl.fetcher.activeDeadlineSeconds | int | `1800` | Seconds a fetch may run before it is killed (30 lists take a few minutes). |
| tsl.fetcher.backoffLimit | int | `2` | Retries of a failed fetch within one schedule slot (a new pod each time). Not for a run that ended below the mandatory territories (exit 1) or published from a last copy because a download failed (exit 3): those fail the Job at once (podFailurePolicy), a retry seconds later changes nothing, and the cluster's KubeJobFailed-style alert then says so. |
| tsl.fetcher.bootstrapJob | bool | `true` | Also run one fetch as a Job at install and whenever the fetcher pod changes (image, config), so a fresh install has a snapshot without waiting for the schedule. Named after a hash of the pod spec; the finished Job stays until the next change replaces it. |
| tsl.fetcher.env | list | `[]` | Extra environment variables for the fetcher only; it already gets `env`, the ConfigMap and `runtime.javaToolOptions` (same profile and settings as the webapp, which is what makes its snapshot the one the webapp expects). |
| tsl.fetcher.existingSecret | string | `""` | Secret whose keys become the fetcher's environment variables. The fetcher does not get the webapp's `envFrom.existingSecret`: it parses thirty documents from the internet and should hold nothing but what that needs (truststore passwords, if you changed them), not the report-signing key's. Empty: no Secret. |
| tsl.fetcher.failedJobsHistoryLimit | int | `3` | Failed fetch Jobs kept. |
| tsl.fetcher.keepSnapshots | int | `3` | Snapshots kept besides the current one (siva.tsl.fetcher.keepSnapshots); a pod in the middle of loading an older one is not pulled away. |
| tsl.fetcher.period | string | `"PT6H"` | How often the trusted lists are fetched (ISO-8601 duration, at most P7D). The schedule and `tsl.snapshot.maxAge` derive from it, and the fetcher writes it into every snapshot (siva.tsl.fetcher.period), from where the pods publish it as siva_tsl_refresh_period_seconds and the trust-list alerts size themselves (table in the deployment guide, "Choosing the fetch period"). Lists can run out within three months of issue, so a longer period is refused at render time. |
| tsl.fetcher.resources | object | requests 250m / 512Mi, limit 1Gi memory | Resources of the fetcher pod; the JVM is sized from the memory limit as for the webapp. |
| tsl.fetcher.schedule | string | derived from period | Cron schedule of the fetch. Empty: derived from `period` (minutes dividing an hour, hours dividing a day, P1D at 03:00, P7D Mondays 03:00). When you set it, keep `period` saying what it means: the alerts are sized from `period`, not from this. |
| tsl.fetcher.startingDeadlineSeconds | string | `""` | Seconds after a missed schedule the fetch may still start; empty for the default. |
| tsl.fetcher.successfulJobsHistoryLimit | int | `3` | Finished fetch Jobs kept for `kubectl get jobs` and the alert rule. |
| tsl.fetcher.timeZone | string | `""` | IANA time zone of the schedule (Kubernetes 1.27+); empty for the controller's. |
| tsl.fetcher.ttlSecondsAfterFinished | string | `""` | Seconds a finished fetch Job is kept (its pod and log with it) before the TTL controller deletes it; empty keeps them until the history limits below rotate them out. A Failed Job is what the standard KubeJobFailed alert fires on, and its log is what names the host that failed, so leave this empty unless your logs are shipped elsewhere. Never applied to the bootstrap Job (a GitOps controller would recreate and re-run it). |
| tsl.fetcher.workVolume | object | `{}` | Working cache the fetcher downloads into, kept between runs so that a list whose download fails keeps its last copy: a volume source as above, or empty for a `work/` directory inside the store. |
| tsl.mode | string | `"online"` | `online`: every pod downloads the lists itself at startup and on SiVa's nightly cron (needs HTTPS egress to ~30 hosts, minutes until ready, replicas started at different times can hold different trust states). `snapshot`: a CronJob running this image in SiVa's `tsl-fetch` profile downloads them, publishes a snapshot into a shared volume and every pod reads that (ready in seconds, identical trust state, no egress to the list hosts from the webapp); the fetcher publishes only when the mandatory territories came through, so `current` always names the last good snapshot. Needs a SiVa build with the fetcher (3.11.0+). |
| tsl.snapshot.maxAge | string | max(4 x period, PT48H) | Refuse a snapshot older than this (ISO-8601 duration; siva.tsl.loader.snapshotMaxAge) and keep the loaded state: a store that stopped being refreshed, or was pointed at an old snapshot by whoever can write it, cannot feed a fresh pod lists older than you allow. It only stops new pods from loading (a pod with nothing loaded stays not-ready), so it is lenient on purpose and the alerts catch staleness earlier. Empty: max(4 x tsl.fetcher.period, 48 h); `none` disables the check. |
| tsl.snapshot.mountPath | string | `"/var/siva/tsl"` | Where the snapshot store is mounted (read-only in the webapp pods, read-write in the fetcher). |
| tsl.snapshot.persistentVolumeClaim.accessModes | list | `["ReadWriteMany"]` | Access modes of the claim. |
| tsl.snapshot.persistentVolumeClaim.annotations | object | `{}` | Claim annotations. |
| tsl.snapshot.persistentVolumeClaim.create | bool | `false` | Create the claim (`<release>-tsl`, kept on uninstall). ReadWriteMany when the fetcher and the webapp pods may land on different nodes; ReadWriteOnce is enough on a single node (kind). |
| tsl.snapshot.persistentVolumeClaim.size | string | `"1Gi"` | Requested size; a snapshot is about 10 MB, keepSnapshots + 1 of them are kept. |
| tsl.snapshot.persistentVolumeClaim.storageClassName | string | `""` | StorageClass; empty for the cluster default. |
| tsl.snapshot.pollInterval | string | `"PT1M"` | How often a pod checks `current` for a new snapshot (ISO-8601 duration; siva.tsl.loader.snapshotPollInterval). Replicas converge within this of a publish; the nightly refresh stays as the safety net. |
| tsl.snapshot.volume | object | `{}` | The store: a volume source without the `name`, for example `persistentVolumeClaim: {claimName: siva-tsl}` for a ReadWriteMany claim you created, or a CSI bucket mount. Ignored when persistentVolumeClaim.create is on. The fetcher renames files and directories in it, so the filesystem must support rename; an object-store mount that does not (mountpoint-s3) cannot hold it. |
| tsl.territories.mandatory | list | `[]` | Countries that must have trust anchors before a pod is ready and before the fetcher publishes (siva.tsl.loader.mandatoryTerritories); their absence alerts as critical, other trusted countries' as warning. Empty: SiVa's default, `EE` (`EE_T` under the `test` profile). |
| tsl.territories.trusted | list | `[]` | Countries whose trusted lists SiVa downloads and trusts (siva.tsl.loader.trustedTerritories), for webapp and fetcher alike. Empty: SiVa's default list (the `test` profile: everything the test LOTL points to). Set them here rather than in config.applicationYaml; the alerts read SiVa's resolved configuration from its metrics either way. |

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

## Dashboards and alerts that pair with SiVa

The chart ships only what needs SiVa's knowledge: the trust-list and error-rate alerts
(`monitoring.prometheusRule`) and one overview dashboard (`monitoring.grafanaDashboard`). For
the rest, use what your monitoring stack and the Grafana community already maintain:

| Need | Use | Notes |
|---|---|---|
| JVM in depth | [JVM (Micrometer)](https://grafana.com/grafana/dashboards/4701), ID 4701 | 24 of its 27 panels fill against SiVa 3.11. Thread utilisation needs `server.tomcat.mbeanregistry.enabled: true` in `config.spring`; process memory needs micrometer-jvm-extras, which SiVa does not include; the errors panel stays empty until a request fails with 5xx. |
| Spring Boot overview by namespace | [Spring Boot 3.x Statistics](https://grafana.com/grafana/dashboards/19004), ID 19004 | Its six HikariCP panels stay empty: SiVa has no database. |
| Pod CPU, memory against limits, restarts | kube-prometheus-stack's "Kubernetes / Compute Resources / Pod" | Ships with the stack. |
| Pod not ready, crash loops | kube-prometheus-stack's `KubePodNotReady`, `KubePodCrashLooping` | The chart does not repeat them; `SivaOomKilled` stays, the stack sees an OOM kill only as a crash loop. |
| A fetch Job that failed | kube-prometheus-stack's `KubeJobFailed` | The fetcher fails its Job when it could not fetch (see "Fetch Job outcomes"); the chart's `SivaTslListNotDownloaded` then names the list. |

Both community dashboards select by an `application` label. `monitoring.serviceMonitor` adds
it (the release's full name) unless `applicationLabel` is false; without the operator, set
`management.metrics.tags.application` in SiVa's config instead.

## Known limits

- Readiness waits for the mandatory trusted lists (`siva.tsl.loader.mandatoryTerritories`, `EE`
  by default); every other territory can be missing or stale without taking the pod out of
  rotation; `SivaTslTerritoryNotValidating` (warning) says so, from SiVa's `siva_tsl_trust_anchors`
  gauge, and `SivaTslMandatoryTerritoryNotValidating` (critical) for the mandatory ones.
- In online mode every replica fetches the trust lists itself; replicas started at different
  times can hold different trust states until the next refresh. Snapshot mode (see "Trust
  lists") removes that, at the price of a shared volume and a CronJob.
- Metrics are at `<release>-management:8081/monitoring/prometheus` (the service port when
  `runtime.managementPort` equals `service.containerPort`); `monitoring.serviceMonitor` scrapes
  it with the Prometheus operator, otherwise scrape it yourself. The trust-list rules use SiVa's
  `siva_tsl_*` gauges, which exist for exactly the configured countries and mark the mandatory
  ones, so the rules carry no country list; request-size rejections and OCSP/TSA failures have
  no metric yet (`monitoring.prometheusRule.pendingRules`).
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
  `runtime.shutdownTimeout`; the chart refuses to render otherwise. The sleep covers new
  connections only: an idle keep-alive connection a client or ingress still holds to the pod is
  cut when SIGTERM pauses Tomcat, and a request already on its way over it gets no answer. The
  chart therefore turns keep-alive off (`runtime.httpKeepAlive`, `Connection: close` on every
  response); turn it on only for a client that cannot afford a handshake per request, and expect
  one lost request per idle connection per pod stop.
- Put JVM flags in `runtime.javaToolOptions`, not in `env`: an `env` entry named
  `JAVA_TOOL_OPTIONS` replaces the chart's.
- Trust material comes from the classpath unless `trust.*` is set; the JVM's own truststore
  (OCSP, AIA, timestamp clients) is only reachable through the Paketo binding, at launch.
