-- Add a boolean flag to mark test users. Use IF NOT EXISTS so the migration is idempotent.
-- Add a boolean flag to mark test users.
-- Use a plain ADD COLUMN - MySQL's support for `IF NOT EXISTS` on ADD COLUMN
-- varies by server version and can cause a syntax error. The migration
-- should run once in a controlled deployment so a plain ALTER is safer.
ALTER TABLE users
  ADD COLUMN is_test BOOLEAN NOT NULL DEFAULT FALSE;
