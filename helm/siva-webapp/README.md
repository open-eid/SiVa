# siva-webapp Helm chart

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

`image.tag` is a string; use `--set-string` for numeric-looking tags (a git short sha such as
`1234567` would otherwise be parsed as a number and rejected by the values schema).

## Values

| Key | Default | Meaning |
|---|---|---|
| `replicaCount` | `1` | Pods. Each one loads trust lists on its own. |
| `image.repository`, `image.tag` | `siva-webapp`, `""` (= `appVersion`) | Your image. Set the repository; the tag defaults to the SiVa release the chart is for. |
| `image.pullPolicy`, `image.pullSecrets` | `IfNotPresent`, `[]` | |
| `service.type`, `service.port`, `service.nodePort` | `ClusterIP`, `8080`, unset | `nodePort` only applies to `NodePort`. |
| `config.applicationYaml` | probes enabled | Spring config override; mounted and passed via `SPRING_CONFIG_ADDITIONAL_LOCATION`. `siva.*` keys: `docs/siva3/deployment_guide.md`. |
| `runtime.managementPort` | `8081` | Actuator (health, probes, Prometheus) on its own port, exposed only by the in-cluster `<release>-management` Service. Set equal to `service.containerPort` to keep it on the traffic port. |
| `runtime.shutdownTimeout`, `runtime.preStopSleepSeconds` | `30s`, `5` | Graceful shutdown: drain budget for in-flight validations (whole seconds, `30s`), and the preStop pause before SIGTERM. `terminationGracePeriodSeconds` must exceed their sum. |
| `runtime.forwardHeadersStrategy` | `framework` | `server.forward-headers-strategy`; `none` when nothing in front sets `X-Forwarded-*`. |
| `runtime.javaToolOptions` | `-XX:+ExitOnOutOfMemoryError` | `JAVA_TOOL_OPTIONS`; Paketo appends its memory flags. |
| `env`, `envFrom.existingSecret` | `[]`, `""` | Extra env vars; a Secret to source env from. |
| `probes.startup/readiness/liveness` | actuator paths under `/monitoring/health` | All timings overridable. Startup budget 10 min by default. |
| `resources` | req 500m/1Gi, limit 2Gi | Paketo sizes the heap from the memory **limit**. |
| `security.podSecurityContext`, `security.securityContext` | Pod Security "restricted" | runAsNonRoot, RuntimeDefault seccomp, no privilege escalation, all capabilities dropped, read-only root (only `/tmp` is writable, an emptyDir). Overriding a field relaxes the profile; enforce it with the namespace label. |
| `tmpSizeLimit` | `1Gi` | Size limit of the `/tmp` emptyDir (trust-list caches, temp files); the pod is evicted when it fills, so keep headroom. Empty for none. |
| `security.automountServiceAccountToken` | `false` | |
| `network.ingresses` | `[]` | List of Ingress objects (`name`, `className`, `annotations`, `hosts`, `tls`). Set the body-size annotation of your controller to at least 27 MB. |
| `serviceAccount.create/name/annotations` | `true`, `""`, `{}` | |
| `nodeSelector`, `tolerations`, `affinity` | empty | |
| `topologySpreadConstraints` | soft spread over nodes | Plain constraints; `labelSelector` defaults to the chart's pods. `[]` disables. |
| `terminationGracePeriodSeconds` | `60` | Greater than preStop sleep + shutdown timeout. |
| `tests.enabled`, `tests.image`, `tests.runAsUser` | `true`, `busybox:1.37.0`, `65534` | `helm test` pod hitting `/monitoring/health` on the management Service; runs as the given uid under the same restricted context. |
| `trust.*`, `scaling.hpa.*`, `network.networkPolicy.*`, `monitoring.serviceMonitor.*` | disabled | Placeholders; keys reserved, no templates yet. |

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

- Readiness is reported before the trust lists are loaded; the long `startupProbe` budget is the
  compensation, and a pod can receive traffic it cannot yet validate. An application change is
  planned to gate readiness on the TSL.
- Health never turns DOWN when a trust-list refresh fails; alerting is left to you.
- Every replica fetches the trust lists itself; replicas started at different times can hold
  different trust states until the next refresh.
- No metrics objects; scrape `<release>-management:8081/monitoring/prometheus` yourself (the
  service port when `runtime.managementPort` equals `service.containerPort`).
- Health, probes and metrics live on the management port (`8081` by default) and are reachable
  only inside the cluster through `<release>-management`; the traffic port answers 404 under
  `/monitoring`. SiVa's own system tests expect them on the traffic port, so a test instance
  sets `runtime.managementPort` to the traffic port (see `values-kind.yaml`). The probes talk
  to the management connector, so a wedged traffic connector does not fail readiness.
- Graceful shutdown needs `sleep` in the image for the preStop pause (Paketo images have it;
  set `runtime.preStopSleepSeconds: 0` otherwise) and a `terminationGracePeriodSeconds` above
  sleep + `runtime.shutdownTimeout`; the chart refuses to render otherwise.
- Put JVM flags in `runtime.javaToolOptions`, not in `env`: an `env` entry named
  `JAVA_TOOL_OPTIONS` replaces the chart's.
- Trust material is the classpath default; rotating it means a new image.
