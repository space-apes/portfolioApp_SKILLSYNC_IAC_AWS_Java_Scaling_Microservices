--liquibase formatted sql
--changeset soosh:001-create-users-table
CREATE TABLE IF NOT EXISTS users (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(150) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--changeset soosh:002-seed-users
INSERT INTO users (name, email, created_at) VALUES ('Alice Tester','alice@example.com', NOW());
INSERT INTO users (name, email, created_at) VALUES ('Bob Dev','bob@example.com', NOW());
INSERT INTO users (name, email, created_at) VALUES ('Carol QA','carol@example.com', NOW());
INSERT INTO users (name, email, created_at) VALUES ('Dave Ops','dave@example.com', NOW());
INSERT INTO users (name, email, created_at) VALUES ('Eve Eng','eve@example.com', NOW());
