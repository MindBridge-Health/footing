CREATE TABLE IF NOT EXISTS organization
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS ec_user
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    version INT,
    lastname VARCHAR NOT NULL,
    firstname VARCHAR NOT NULL,
    middlename VARCHAR
);

CREATE TABLE IF NOT EXISTS benefactor
(
    id   UUID PRIMARY KEY REFERENCES ec_user(id)
);

CREATE TABLE IF NOT EXISTS chronicler
(
    id   UUID PRIMARY KEY REFERENCES ec_user(id),
    is_ai BOOLEAN
);

CREATE TABLE IF NOT EXISTS storyteller
(
    id   UUID PRIMARY KEY REFERENCES ec_user(id),
    preferred_chronicler_id UUID,
    contact_method VARCHAR,
    FOREIGN KEY (preferred_chronicler_id) REFERENCES chronicler(id)
);

CREATE TABLE IF NOT EXISTS storyteller_benefactor_link
(
    storyteller_id UUID,
    benefactor_id UUID
);

CREATE TABLE IF NOT EXISTS resource
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR NOT NULL,
    text VARCHAR
);

CREATE TABLE IF NOT EXISTS resource_tag_link
(
    resource_id UUID,
    tag_id UUID,
    FOREIGN KEY (resource_id) REFERENCES resource(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE IF NOT EXISTS story
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    storyteller_id UUID,
    text VARCHAR,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id)
);

CREATE TABLE IF NOT EXISTS media
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    location VARCHAR,
    type VARCHAR,
    story_id UUID,
    FOREIGN KEY (story_id) REFERENCES story(id)
);

CREATE TABLE IF NOT EXISTS story_group
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    storyteller_id UUID
);

CREATE TABLE IF NOT EXISTS story_group_story_link
(
    story_group_id UUID,
    story_id UUID,
    FOREIGN KEY (story_group_id) REFERENCES story_group(id),
    FOREIGN KEY (story_id) REFERENCES story(id)
);

CREATE TABLE IF NOT EXISTS question
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    text VARCHAR,
    custom BOOLEAN
);

CREATE TABLE IF NOT EXISTS interview
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    storyteller_id UUID,
    chronicler_id UUID,
    time_completed DATETIME,
    completed BOOLEAN,
    FOREIGN KEY (storyteller_id) REFERENCES storyteller(id),
    FOREIGN KEY (chronicler_id) REFERENCES chronicler(id)
);

CREATE TABLE IF NOT EXISTS interview_question
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    question_id UUID,
    interview_id UUID,
    story_id UUID,
    completed BOOLEAN,
    skipped BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES question(id),
    FOREIGN KEY (interview_id) REFERENCES interview(id),
    FOREIGN KEY (story_id) REFERENCES story(id)
);

CREATE TABLE IF NOT EXISTS scheduled_interview
(
    id   UUID PRIMARY KEY REFERENCES resource(id),
    scheduled_time DATETIME,
    interview_id UUID,
    FOREIGN KEY (interview_id) REFERENCES interview(id)
);

CREATE TABLE IF NOT EXISTS access_policy
(
    id   UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS access_policy_user_link
(
    access_policy_id UUID PRIMARY KEY REFERENCES access_policy(id),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES ec_user(id)
);

CREATE TABLE IF NOT EXISTS access_policy_allowed_resource_link
(
    access_policy_id UUID REFERENCES access_policy(id),
    resource_id UUID,
    PRIMARY KEY (access_policy_id, resource_id)
);

CREATE TABLE IF NOT EXISTS access_policy_denied_resource_link
(
    access_policy_id UUID REFERENCES access_policy(id),
    resource_id UUID,
    PRIMARY KEY (access_policy_id, resource_id)
);