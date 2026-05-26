CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    team_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_team_id ON incidents(team_id);

INSERT INTO incidents (id, title, description, severity, status, team_id, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'API latency above SLO', 'Gateway p95 latency is above the expected SLO.', 'HIGH', 'OPEN', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222222222', 'Database backup warning', 'Nightly database backup took longer than expected.', 'MEDIUM', 'IN_PROGRESS', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW(), NOW()),
    ('33333333-3333-3333-3333-333333333333', 'Container image CVE', 'A critical CVE was detected in an application image.', 'CRITICAL', 'OPEN', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW());

