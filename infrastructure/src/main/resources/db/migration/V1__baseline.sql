-- The schema as SchemaUtils.create built it before Flyway, generated from the Exposed tables.
-- A database that already has these tables (extopy.dev) is baselined at this version instead.

CREATE TABLE IF NOT EXISTS ClientsInUsers (code VARCHAR(32) PRIMARY KEY, user_id BINARY(16) NOT NULL, client_id BINARY(16) NOT NULL, expiration DATETIME(6) NOT NULL);
CREATE TABLE IF NOT EXISTS Sessions (id VARCHAR(255) PRIMARY KEY, `value` text NOT NULL);
CREATE TABLE IF NOT EXISTS Users (id BINARY(16) PRIMARY KEY, displayname VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, biography text NOT NULL, avatar VARCHAR(255) DEFAULT 'https://extopy.com/img/avatar.png' NOT NULL, birthdate DATE NOT NULL, join_date DATETIME(6) NOT NULL, last_active DATETIME(6) NOT NULL, personal BOOLEAN DEFAULT FALSE NOT NULL, verified BOOLEAN DEFAULT FALSE NOT NULL, banned BOOLEAN DEFAULT FALSE NOT NULL);
CREATE TABLE IF NOT EXISTS LikesInPosts (post_id BINARY(16), user_id BINARY(16), CONSTRAINT pk_LikesInPosts PRIMARY KEY (post_id, user_id));
CREATE INDEX LikesInPosts_post_id ON LikesInPosts (post_id);
CREATE INDEX LikesInPosts_user_id ON LikesInPosts (user_id);
CREATE TABLE IF NOT EXISTS Posts (id BINARY(16) PRIMARY KEY, user_id BINARY(16) NOT NULL, replied_to_id BINARY(16) NULL, repost_of_id BINARY(16) NULL, body text NOT NULL, published_at DATETIME(6) NOT NULL, edited_at DATETIME(6) DEFAULT NULL NULL, expires_at DATETIME(6) NOT NULL, visibility text NOT NULL);
CREATE INDEX Posts_user_id ON Posts (user_id);
CREATE TABLE IF NOT EXISTS CodesInEmails (email VARCHAR(255) PRIMARY KEY, code VARCHAR(32) NOT NULL, expires_at DATETIME(6) NOT NULL);
CREATE INDEX CodesInEmails_code ON CodesInEmails (code);
CREATE TABLE IF NOT EXISTS FollowersInUsers (user_id BINARY(16), target_id BINARY(16), accepted BOOLEAN NOT NULL, CONSTRAINT pk_FollowersInUsers PRIMARY KEY (user_id, target_id));
CREATE INDEX FollowersInUsers_user_id ON FollowersInUsers (user_id);
CREATE INDEX FollowersInUsers_target_id ON FollowersInUsers (target_id);
CREATE TABLE IF NOT EXISTS Clients (id BINARY(16) PRIMARY KEY, owner_id BINARY(16) NOT NULL, `name` VARCHAR(255) NOT NULL, description text NOT NULL, secret VARCHAR(255) NOT NULL, redirect_uri text NOT NULL);
CREATE INDEX Clients_owner_id ON Clients (owner_id);