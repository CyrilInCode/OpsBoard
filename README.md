# OpsBoard DevOps

OpsBoard is a DevOps school project built as a microservice incident management dashboard.

It includes a Spring Boot backend, a TanStack Start frontend, PostgreSQL databases, Docker Compose, CI, tests, JaCoCo, SonarQube and Codecov.

## Architecture

- `backend/gateway`: Spring Cloud Gateway, public API on `8080`
- `backend/incident-service`: incident service, PostgreSQL-backed, port `8081`
- `backend/team-service`: team service, PostgreSQL-backed, port `8082`
- `frontend`: TanStack Start React app, port `3000`
- `docker-compose.yml`: full local stack with app services and databases

## Run

```bash
cp .env.example .env
docker compose up --build
```

Open:

- Frontend: http://localhost:3000
- Gateway health: http://localhost:8080/actuator/health
- Incidents API: http://localhost:8080/api/incidents
- Teams API: http://localhost:8080/api/teams

Stop:

```bash
docker compose down
```

## Checks

Backend:

```bash
mvn -f backend/pom.xml verify
```

Frontend:

```bash
cd frontend
npm ci
npm run typecheck
npm run test:coverage
npm run build
```

Reports:

- JaCoCo HTML: `backend/*/target/site/jacoco/index.html`
- JaCoCo XML: `backend/*/target/site/jacoco/jacoco.xml`
- Frontend LCOV: `frontend/coverage/lcov.info`
- Surefire: `backend/*/target/surefire-reports`
- Checkstyle: `backend/*/target/checkstyle-result.xml`

## Quality Tools

Local SonarQube:

```bash
docker compose --profile quality up -d sonarqube
```

Then open http://localhost:9000.

GitHub Actions runs backend tests, frontend tests, coverage, build, SonarQube Cloud analysis and Codecov upload.

Required GitHub Actions secrets and variables:

- Secret: `SONAR_TOKEN`
- Secret: `CODECOV_TOKEN`
- Variable: `SONAR_PROJECT_KEY`
- Variable: `SONAR_ORGANIZATION`

Detailed setup:

- SonarQube: `docs/sonarqube.md`
- Codecov: `docs/codecov.md`

## Documentation

- Project report: `docs/rapport.md`
- Google labs screenshots placeholder: `docs/google-labs/README.md`
