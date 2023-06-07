DROP DATABASE ECTEST;
CREATE DATABASE IF NOT EXISTS ECTEST;

USE ECTEST;

CREATE TABLE IF NOT EXISTS organization
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS mb_user
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version INT,
    is_active BOOLEAN NOT NULL,
    lastname VARCHAR(128) NOT NULL,
    firstname VARCHAR(128) NOT NULL,
    middlename VARCHAR(128),
    email VARCHAR(128),
    mobile VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS resource
(
    id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    id_text varchar(36) generated always as
             (insert(
                insert(
                        insert(
                                insert(hex(id),9,0,'-'),
                                14,0,'-'),
                        19,0,'-'),
                24,0,'-')
             ) virtual,
    storyteller_id MEDIUMINT,
    text VARCHAR(128),
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id)
);

CREATE TABLE IF NOT EXISTS media
(
    id MEDIUMINT PRIMARY KEY REFERENCES resource(id),

    location VARCHAR(128) NOT NULL,
    type VARCHAR(128),
    storyteller_id MEDIUMINT,
    story_id MEDIUMINT,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id),
    FOREIGN KEY (story_id) REFERENCES story(id)
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
    scheduled_time DATETIME,
    interview_id MEDIUMINT,
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

INSERT IGNORE INTO onboarding_status values (0, 'ONBOARDING_NOT_STARTED' );
INSERT IGNORE INTO onboarding_status values (1, 'ONBOARDING_STARTED' );
INSERT IGNORE INTO onboarding_status values (99, 'ONBOARDING_COMPLETED' );
