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
| `env`, `envFrom.existingSecret` | `[]`, `""` | Extra env vars; a Secret to source env from. |
| `probes.startup/readiness/liveness` | actuator paths under `/monitoring/health` | All timings overridable. Startup budget 10 min by default. |
| `resources` | req 500m/1Gi, limit 2Gi | Paketo sizes the heap from the memory **limit**. |
| `security.podSecurityContext`, `security.securityContext` | restricted-compatible | `readOnlyRootFilesystem` is `false` for now, see comment in values. |
| `security.automountServiceAccountToken` | `false` | |
| `network.ingresses` | `[]` | List of Ingress objects (`name`, `className`, `annotations`, `hosts`, `tls`). Set the body-size annotation of your controller to at least 27 MB. |
| `serviceAccount.create/name/annotations` | `true`, `""`, `{}` | |
| `nodeSelector`, `tolerations`, `affinity` | empty | |
| `terminationGracePeriodSeconds` | `60` | |
| `tests.enabled`, `tests.image` | `true`, `busybox:1.37.0` | `helm test` pod hitting `/monitoring/health`. |
| `trust.*`, `scaling.hpa.*`, `network.networkPolicy.*`, `monitoring.serviceMonitor.*` | disabled | Placeholders; keys reserved, no templates yet. |

## Verifying on a NodePort (kind or any node you can reach)

```sh
helm install siva ./helm/siva-webapp --set image.repository=<your-registry>/siva-webapp \
  --set service.type=NodePort --set service.nodePort=30080
kubectl rollout status deployment/siva-siva-webapp --timeout=15m   # trust lists download first
curl http://<node>:30080/monitoring/health
curl http://<node>:30080/monitoring/version
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
- No metrics objects; scrape `/monitoring/prometheus` on the service port yourself.
- No graceful shutdown in the application yet: in-flight validations are cut at SIGTERM after
  `terminationGracePeriodSeconds`.
- Trust material is the classpath default; rotating it means a new image.
