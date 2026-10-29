# SGV deployment (GitOps with Rancher Fleet)

- `base/`: manifests shared by all environments
- `overlays/prod/`: production (namespace `sgv-prod`, image tags)
- `cluster/`: cluster-scoped objects (cert-manager self-signed ClusterIssuer)
- `../ops/`: one-off jobs run by hand (restore test)

Fleet (Rancher > Continuous Delivery) applies `deploy/` automatically after every merge to `main`.
**To release a new version:** open a PR changing `newTag` in `overlays/prod/kustomization.yaml`.

## Not stored in Git (create once per environment)

    kubectl -n sgv-prod create secret generic sgv-db \
      --from-literal=POSTGRES_USER=sgv_admin \
      --from-literal=POSTGRES_PASSWORD=<generated> \
      --from-literal=API_DB_USER=sgv_api \
      --from-literal=API_DB_PASSWORD=<generated>
