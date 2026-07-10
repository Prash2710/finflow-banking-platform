INSERT INTO auth.roles
(id, role_name, description, created_at)
VALUES
(gen_random_uuid(), 'CUSTOMER', 'Bank Customer', CURRENT_TIMESTAMP),

(gen_random_uuid(), 'ADMIN', 'System Administrator', CURRENT_TIMESTAMP),

(gen_random_uuid(), 'EMPLOYEE', 'Bank Employee', CURRENT_TIMESTAMP),

(gen_random_uuid(), 'AUDITOR', 'Audit Team', CURRENT_TIMESTAMP);