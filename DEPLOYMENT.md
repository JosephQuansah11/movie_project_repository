# CI/CD and VM deployment

## GitHub configuration

Create a GitHub Environment named `production` and add these secrets:

- `VM_HOST`, `VM_USERNAME`, `VM_SSH_PRIVATE_KEY`, optionally `VM_SSH_PORT`
- `GHCR_USERNAME` and a read-only `GHCR_TOKEN` for the VM to pull private images

Add these non-secret Environment variables for the frontend build:

- `PUBLIC_JAVA_API_URL`, `PUBLIC_PYTHON_API_URL`, `PUBLIC_KEYCLOAK_URL`
- `KEYCLOAK_REALM`, `KEYCLOAK_FRONTEND_CLIENT_ID`

The workflow uses the automatic `GITHUB_TOKEN` to publish images. No database or Keycloak password is stored in GitHub Actions logs or source control.

## Production VM

Install Docker Engine and Compose on the VM, create `/opt/demo`, and copy `docker-compose.production.yml` plus `.env.production` there. Generate `.env.production` from [.env.production.example](.env.production.example), replace every placeholder, and restrict it with `chmod 600 .env.production`.

The compose stack runs PostgreSQL with a persistent volume, Keycloak, the Java API, the Python data API, and nginx serving the frontend. For a managed online PostgreSQL database, set `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` in the Java service environment and remove the local `database` dependency from the compose file. Backups, TLS termination, DNS, and firewall rules remain VM/cloud-provider responsibilities.

The deploy job pulls immutable images tagged with the commit SHA, then recreates the stack. Configure the VM firewall or reverse proxy so only the public frontend and required API/auth endpoints are exposed.

## Render from GitHub

The repository includes [render.yaml](render.yaml). In Render, choose **New > Blueprint**, connect the GitHub repository, select the branch, and apply the Blueprint. Render will build and auto-deploy the Java API, Python API, frontend static site, and managed PostgreSQL database whenever the tracked branch changes.

After the Blueprint is created, set the `sync: false` values in the Render dashboard:

- Java: `KEYCLOAK_ISSUER_URI`, `KEYCLOAK_SERVER_URL`, `KEYCLOAK_REALM`, `KEYCLOAK_ADMIN_CLIENT_ID`, `KEYCLOAK_ADMIN_USERNAME`, `KEYCLOAK_ADMIN_PASSWORD`
- Frontend: `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, `VITE_KEYCLOAK_CLIENT_ID`

Configure the Keycloak client redirect URI and web origin for `https://demo-frontend.onrender.com` (or your custom frontend domain). The Keycloak issuer must be publicly reachable by both the browser and Java API. The Render free PostgreSQL plan is suitable for development only; use a paid database and backups for production. Set `JPA_DDL_AUTO=validate` after introducing migrations.