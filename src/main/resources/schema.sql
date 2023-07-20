DROP DATABASE IF EXISTS ECTEST;
CREATE DATABASE IF NOT EXISTS ECTEST;

USE ECTEST;

CREATE TABLE IF NOT EXISTS organization
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

-- ToDo link organization to mb_user

CREATE TABLE IF NOT EXISTS mb_user
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    alt_id VARCHAR(128) UNIQUE NOT NULL,
    version INT,
    is_active BOOLEAN NOT NULL,
    lastname VARCHAR(128),
    firstname VARCHAR(128),
    middlename VARCHAR(128),
    email VARCHAR(128),
    mobile VARCHAR(32),
    INDEX(alt_id)
);

CREATE TABLE IF NOT EXISTS resource
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    alt_id VARCHAR(128) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    is_deleted BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    text VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS resource_tag_link
(
    resource_id MEDIUMINT,
    tag_id MEDIUMINT,
    FOREIGN KEY (resource_id) REFERENCES resource(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE IF NOT EXISTS onboarding_status
(
    id INT PRIMARY KEY,
    status VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS benefactor
(
    id MEDIUMINT PRIMARY KEY REFERENCES mb_user(id)
);

CREATE TABLE IF NOT EXISTS chronicler
(
    id MEDIUMINT PRIMARY KEY REFERENCES mb_user(id),
    is_ai BOOLEAN
);

CREATE TABLE IF NOT EXISTS storyteller
(
    id MEDIUMINT PRIMARY KEY REFERENCES mb_user(id),
    preferred_chronicler_id MEDIUMINT,
    contact_method VARCHAR(128),
    onboarding_status int,
    FOREIGN KEY (preferred_chronicler_id) REFERENCES chronicler(id),
    FOREIGN KEY (onboarding_status) REFERENCES  onboarding_status(id)
);

CREATE TABLE IF NOT EXISTS storyteller_benefactor_link
(
    storyteller_id MEDIUMINT,
    benefactor_id MEDIUMINT,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id),
    FOREIGN KEY (benefactor_id) REFERENCES  benefactor(id)
);

CREATE TABLE IF NOT EXISTS preferred_time
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    storyteller_id MEDIUMINT NOT NULL REFERENCES storyteller(id),
    time time NOT NULL,
    day varchar(16) NOT NULL,
    INDEX (storyteller_id),
    UNIQUE INDEX (storyteller_id, time, day)
);

CREATE TABLE IF NOT EXISTS story
(
    id   MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    storyteller_id MEDIUMINT,
    text LONGTEXT,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id)
);

CREATE TABLE IF NOT EXISTS media_status
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    received TIMESTAMP NOT NULL DEFAULT NOW(),
    state VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS media
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    location VARCHAR(128) NOT NULL,
    type VARCHAR(128),
    storyteller_id MEDIUMINT,
    story_id MEDIUMINT,
    raw_json json,
    media_status_id MEDIUMINT,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id),
    FOREIGN KEY (story_id) REFERENCES story(id),
    FOREIGN KEY (media_status_id) REFERENCES media_status(id)
);

CREATE TABLE IF NOT EXISTS story_group
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    storyteller_id MEDIUMINT,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id)
);

CREATE TABLE IF NOT EXISTS story_group_story_link
(
    story_group_id MEDIUMINT,
    story_id MEDIUMINT,
    FOREIGN KEY (story_group_id) REFERENCES story_group(id),
    FOREIGN KEY (story_id) REFERENCES story(id)
);

CREATE TABLE IF NOT EXISTS question
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),

    text VARCHAR(128),
    custom BOOLEAN
);

CREATE TABLE IF NOT EXISTS interview
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),

    storyteller_id MEDIUMINT,
    chronicler_id MEDIUMINT,
    time_completed DATETIME,
    completed BOOLEAN,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id),
    FOREIGN KEY (chronicler_id) REFERENCES chronicler(id)
);

CREATE TABLE IF NOT EXISTS interview_question
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    question_id MEDIUMINT,
    interview_id MEDIUMINT,
    story_id MEDIUMINT,
    completed BOOLEAN,
    skipped BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES question(id),
    FOREIGN KEY (interview_id) REFERENCES interview(id),
    FOREIGN KEY (story_id) REFERENCES story(id)
);

CREATE TABLE IF NOT EXISTS scheduled_interview
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    scheduled_time DATETIME UNIQUE NOT NULL,
    interview_id MEDIUMINT NOT NULL ,
    FOREIGN KEY (interview_id) REFERENCES interview(id)
);

CREATE TABLE IF NOT EXISTS access_policy
(
    id   MEDIUMINT PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS access_policy_user_link
(
    access_policy_id MEDIUMINT PRIMARY KEY REFERENCES access_policy(id),
    user_id MEDIUMINT,
    FOREIGN KEY (user_id) REFERENCES mb_user(id)
);

CREATE TABLE IF NOT EXISTS access_policy_allowed_resource_link
(
    access_policy_id MEDIUMINT REFERENCES access_policy(id),
    resource_id MEDIUMINT,
    PRIMARY KEY (access_policy_id, resource_id)
);

CREATE TABLE IF NOT EXISTS access_policy_denied_resource_link
(
    access_policy_id MEDIUMINT REFERENCES access_policy(id),
    resource_id MEDIUMINT,
    PRIMARY KEY (access_policy_id, resource_id)
);

CREATE TABLE IF NOT EXISTS twilio_status
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    received TIMESTAMP NOT NULL DEFAULT NOW(),
    call_sid varchar(64),
    call_status varchar(32),
    recording_sid varchar(64),
    recording_status varchar(32),
    recording_url varchar(256),
    transcription_sid varchar(64),
    transcription_status varchar(32)
);

CREATE TABLE IF NOT EXISTS twilio_data
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),
    status_id varchar(64) references twilio_status(id),
    interview_question_id varchar(64) references interview_question(id),
    story_id varchar(64) references story(id),
    raw_json json
);

-- Default Chronicler
INSERT IGNORE INTO mb_user values (default, '0', 0, true, 'Chat', 'GPT2.0', null, null, null);
SET @lastId := LAST_INSERT_ID();
INSERT IGNORE INTO chronicler values (@lastId, true);

-- Onboarding Statuses
INSERT IGNORE INTO onboarding_status values (0, 'ONBOARDING_NOT_STARTED' );
INSERT IGNORE INTO onboarding_status values (1, 'ONBOARDING_STARTED' );
INSERT IGNORE INTO onboarding_status values (99, 'ONBOARDING_COMPLETED' );

-- Initial Questions
INSERT INTO resource values (default, 'biq1', 'Initial Question 1', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'What is your earliest childhood memory that still stands out to you today?', false);

INSERT INTO resource values (default, 'biq2', 'Initial Question 2', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'Can you recall a significant event from your teenage years that had a profound impact on your life?', false);

INSERT INTO resource values (default, 'biq3', 'Initial Question 3', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'What is the most memorable trip or vacation you have ever taken, and what made it so special?', false);

INSERT INTO resource values (default, 'biq4', 'Initial Question 4', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'Who was the most influential person in your life, and how did they shape you into who you are today?', false);

INSERT INTO resource values (default, 'biq5', 'Initial Question 5', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'Is there a particular hobby or activity that brings you joy and relaxation? How did you discover it?', false);

INSERT INTO resource values (default, 'biq6', 'Initial Question 6', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'What was your proudest accomplishment in your professional life, and what obstacles did you overcome to achieve it?', false);

INSERT INTO resource values (default, 'biq7', 'Initial Question 7', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'Can you share a story of a friendship or relationship that has lasted for many years and holds a special place in your heart?', false);

INSERT INTO resource values (default, 'biq8', 'Initial Question 8', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'What is a valuable life lesson you have learned over the years, and how did it impact your outlook on life?', false);

INSERT INTO resource values (default, 'biq9', 'Initial Question 9', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'Are there any historical events or moments that you vividly remember and how they influenced your perspective?', false);

INSERT INTO resource values (default, 'biq10', 'Initial Question 10', false);
SET @lastId := LAST_INSERT_ID();
INSERT INTO question values (@lastId, 'If you could give one piece of advice to younger generations, what would it be and why?', false);