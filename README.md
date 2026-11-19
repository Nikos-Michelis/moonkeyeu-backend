# MoonkeyEU Backend

The MoonkeyEU backend is made of two Spring Boot services. They share one MySQL database and run in Docker behind Nginx, with a Grafana monitoring stack alongside.

| Service | Spring Boot | Java | Port | Purpose |
|---|---|---|---|---|
| `moonkeyeu-core-api` | 3.5.14 | 21 | 8080 | Public REST API: launches, users, auth (JWT + Google OAuth2), mail |
| `moonkeyeu-etl-api` | 4.1.2 | 21 | 8081 | ETL pipeline: pulls data from The Space Devs (LL2) and stores media in S3/CloudFront |

Both services serve under the `/api/v1/` context path and expose `/api/v1/actuator/health`.

> **Roadmap:** `core-api` will move to Spring Boot 4, and its security will be rebuilt on Keycloak. `etl-api` is already on Spring Boot 4 and is actively maintained.

## Repository layout

```
moonkeyeu-core-api/       Core REST API (Maven project)
moonkeyeu-etl-api/        ETL service (Maven project, Spring Batch)
db/migration/             Flyway migrations (schema, seed, init scripts)
jwt-keys/                 RSA key pairs for access/refresh tokens (git-ignored)
nginx/                    Reverse proxy image, configs, certbot volumes
monitoring/               Prometheus, Loki, Alloy and Grafana configs and dashboards
ansible/                  VPS provisioning and deploy playbooks
scripts/                  Certificate and MySQL backup helpers
docker-compose.dev.yml    Local stack (builds images from source)
docker-compose.prod.yml   Production stack (pulls images from AWS ECR)
Makefile                  Certbot / Nginx certificate commands
```

## Running locally

### Prerequisites

- Docker with Compose v2 (`docker compose version`)
- JDK 21, only if you want to run a service outside Docker

### 1. Configure the environment

Copy [`example.env`](example.env) to `.env` (git-ignored) and fill in your values:

```bash
cp example.env .env
```

The file lists every variable the compose files read, grouped and commented.

### 2. Add the JWT keys

`core-api` mounts `./jwt-keys` read-only and expects these files:

```
jwt-keys/access-token/access-token-private.pem
jwt-keys/access-token/access-token-public.pem
jwt-keys/refresh-token/refresh-token-private.pem
jwt-keys/refresh-token/refresh-token-public.pem
```

You can generate a pair with OpenSSL. Repeat for `refresh-token`:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out jwt-keys/access-token/access-token-private.pem
openssl rsa -in jwt-keys/access-token/access-token-private.pem -pubout -out jwt-keys/access-token/access-token-public.pem
```

### 3. AWS credentials for the ETL service

`etl-api` mounts AWS credentials from `/home/moonkeyeu/.aws`. On your own machine, point that volume in `docker-compose.dev.yml` at your local `~/.aws` instead. The commented-out line next to it shows how.

### 4. Start the stack

```bash
docker compose -f docker-compose.dev.yml up --build -d
```

Startup order is enforced with health checks: **MySQL → Flyway migrations → core-api → etl-api → Prometheus → Alloy → Loki → Grafana → Nginx**.

Useful commands:

```bash
docker compose -f docker-compose.dev.yml ps                        # status and health
docker compose -f docker-compose.dev.yml logs -f moonkeyeu-core-api
docker compose -f docker-compose.dev.yml down                      # stop (volumes are kept)
```

### Exposed ports (dev)

| Port | Service |
|---|---|
| 83 | Nginx (entry point to the APIs and Grafana) |
| 8081 | etl-api |
| 9090 | Prometheus |
| 3100 | Loki |
| 12345 | Alloy |

## Running a service without Docker

Start MySQL first (for example `docker compose -f docker-compose.dev.yml up -d mysqldb flyway`). Then export the same variables and run:

```bash
cd moonkeyeu-core-api        # or moonkeyeu-etl-api
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw verify
```

Tests use Testcontainers (MySQL, and S3Mock for `etl-api`), so Docker must be running. JaCoCo writes coverage reports to `target/site/jacoco/`.

## Database migrations

Flyway runs as its own container before either service starts, applying the scripts in `db/migration/`. It connects as `MYSQL_MIGRATION_USER`, while the services connect as `MYSQL_APP_USER`. To change the schema, add a new versioned script. Never edit a migration that has already been applied.

## Production

- **CI/CD:** `.github/workflows/pipeline.yml` runs on pushes and PRs to `dev`, `main` and `release/**`, in this order: Semgrep security scan → tests for each module → Docker image build and push to ECR → deploy to prod.
- **Deploy:** `docker-compose.prod.yml` pulls `${APP_NAME}-core-api:${IMAGE_TAG}` and `${APP_NAME}-etl-api:${IMAGE_TAG}` from ECR. The VPS is provisioned and deployed with the Ansible playbooks in `ansible/playbooks/` (`provision.yml`, `deploy.yml`).
- **TLS certificates** are managed through the certbot container:

  ```bash
  make help                # list commands
  make cert-obtain         # first-time certificate
  make cert-renew          # renew certificates close to expiry
  make renew               # renew and reload Nginx
  ```

- **Backups:** `scripts/backup/mysql_backup.sh`.

## Monitoring

Prometheus scrapes the services' `/actuator/prometheus` endpoints, Alloy ships Docker, host and Nginx logs to Loki, and Grafana is provisioned automatically with dashboards for the JVM, Spring Boot, MySQL, Nginx and the VPS (`monitoring/grafana/json/`).

## Key dependencies

| Library | Used in | Purpose |
|---|---|---|
| Spring Security + JJWT | core | Stateless JWT auth (RSA-signed access/refresh tokens), Google OAuth2 |
| Spring Data JPA / Hibernate | both | Persistence on MySQL 8 |
| Flyway | both | Schema migrations |
| Spring Batch | etl | Chunked, retryable ETL jobs with transactional writes |
| WebClient (WebFlux) | both | Non-blocking HTTP calls to external APIs (LL2, NASA APOD) |
| AWS SDK S3 | etl | Media upload, served through CloudFront |
| Caffeine | both | In-memory caching of hot read paths |
| Bucket4j | core | Rate limiting |
| springdoc-openapi | core | OpenAPI / Swagger UI |
| Actuator + Micrometer | both | Health checks and Prometheus metrics |
