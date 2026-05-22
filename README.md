# OpsBoard DevOps

OpsBoard is a complete DevOps school project: a microservice application for tracking incidents, responsible teams, and operational status.

It follows the requested constraints from `DevOps projet.pdf`:

- Git repository structure
- CI pipeline
- Layered backend architecture: data, service, web/controller
- At least two backend services integrated with Docker
- Unit tests, web mock tests, repository tests
- Code coverage with JaCoCo
- Software quality checks with Checkstyle
- Optional bonus: web frontend and PostgreSQL database

## Architecture

- `backend/gateway`: Spring Cloud Gateway, public entry point on port `8080`
- `backend/incident-service`: incident domain service, PostgreSQL-backed, internal port `8081`
- `backend/team-service`: team/on-call domain service, PostgreSQL-backed, internal port `8082`
- `frontend`: TanStack Start React app, port `3000`
- `docker-compose.yml`: full local stack with two PostgreSQL databases

## Run Everything

```bash
cp .env.example .env
docker compose up --build
```

Then open:

- Frontend: http://localhost:3000
- Gateway health: http://localhost:8080/actuator/health
- Incidents API: http://localhost:8080/api/incidents
- Teams API: http://localhost:8080/api/teams

## Backend Checks

If Maven is installed:

```bash
mvn -f backend/pom.xml verify
```

Without local Maven:

```bash
docker run --rm -v "$PWD/backend:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn verify
```

Generated backend reports:

- JaCoCo: `backend/*/target/site/jacoco/index.html`
- Surefire tests: `backend/*/target/surefire-reports`
- Checkstyle: `backend/*/target/checkstyle-result.xml`

## Frontend Checks

```bash
cd frontend
npm install
npm run typecheck
npm test -- --run
npm run build
```

## Written Report

The submission report draft is in `docs/rapport.md`.

