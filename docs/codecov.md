# Codecov Setup

The project publishes two coverage reports:

- Backend Java coverage from JaCoCo XML.
- Frontend TypeScript coverage from Vitest LCOV.

## Local Coverage

Generate backend reports:

```bash
mvn -B -f backend/pom.xml verify
```

Generated files:

```text
backend/gateway/target/site/jacoco/jacoco.xml
backend/incident-service/target/site/jacoco/jacoco.xml
backend/team-service/target/site/jacoco/jacoco.xml
```

Generate frontend coverage:

```bash
cd frontend
npm ci
npm run test:coverage
```

Generated file:

```text
frontend/coverage/lcov.info
```

## GitHub Setup

Create or open the repository on Codecov, then connect it to the GitHub repository.

In GitHub, go to:

```text
Repository > Settings > Secrets and variables > Actions
```

Add this repository secret:

```text
CODECOV_TOKEN
```

Use the token shown in the Codecov repository settings.

The `quality` job in `.github/workflows/ci.yml` uploads:

- `backend` flag: all JaCoCo XML reports.
- `frontend` flag: `frontend/coverage/lcov.info`.

Codecov thresholds, flags, comments, and ignored paths are configured in `codecov.yml`.
