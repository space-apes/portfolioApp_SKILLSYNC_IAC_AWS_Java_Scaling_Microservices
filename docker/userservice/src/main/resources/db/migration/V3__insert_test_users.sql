-- Insert a few test users and mark them with is_test = true.
-- Idempotent inserts using WHERE NOT EXISTS.

INSERT INTO users (first_name, last_name, email, is_test)
SELECT 'Alice', 'Smith', 'alice@example.com', TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@example.com');

INSERT INTO users (first_name, last_name, email, is_test)
SELECT 'Bob', 'Bobson', 'bob@example.com', TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');

INSERT INTO users (first_name, last_name, email, is_test)
SELECT 'Charlie', 'Brown', 'charlie@example.com', TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'charlie@example.com');
