CREATE TABLE teams (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    service_name VARCHAR(120) NOT NULL,
    contact_email VARCHAR(160) NOT NULL,
    on_call_engineer VARCHAR(120) NOT NULL,
    capacity INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_teams_service_name ON teams(service_name);

INSERT INTO teams (id, name, service_name, contact_email, on_call_engineer, capacity, created_at, updated_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Platform Team', 'gateway', 'platform@example.com', 'Nadia Martin', 8, NOW(), NOW()),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Database Team', 'postgresql', 'database@example.com', 'Leo Bernard', 5, NOW(), NOW()),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Security Team', 'supply-chain', 'security@example.com', 'Ines Leroy', 4, NOW(), NOW());

