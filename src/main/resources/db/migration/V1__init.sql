CREATE TABLE auth_token
(
    created_date  datetime(6) NOT NULL,
    id            bigint       NOT NULL AUTO_INCREMENT,
    student_id    bigint       NOT NULL,
    refresh_token varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE avatar
(
    active               bit    NOT NULL,
    logged_out           bit    NOT NULL,
    notification_enabled bit    NOT NULL,
    created_date         datetime(6) NOT NULL,
    id                   bigint NOT NULL AUTO_INCREMENT,
    student_id           bigint NOT NULL,
    updated_date         datetime(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE block
(
    blocked_student_id bigint NOT NULL,
    id                 bigint NOT NULL AUTO_INCREMENT,
    student_id         bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE bookmark
(
    created_date datetime(6) NOT NULL,
    feed_id      bigint NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    student_id   bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE category
(
    id   bigint      NOT NULL AUTO_INCREMENT,
    name varchar(15) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE chat
(
    chatroom_id  bigint NOT NULL,
    created_date datetime(6) NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    student_id   bigint NOT NULL,
    message      TEXT   NOT NULL,
    type         enum ('IMAGE','TALK') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE chat_request
(
    created_date datetime(6) NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    receiver_id  bigint NOT NULL,
    sender_id    bigint NOT NULL,
    updated_date datetime(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE chatroom
(
    created_date      datetime(6) NOT NULL,
    id                bigint NOT NULL AUTO_INCREMENT,
    last_message_time datetime(6) NOT NULL,
    last_message      varchar(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE chatroom_student
(
    exited       bit     NOT NULL,
    unread_count integer NOT NULL,
    chatroom_id  bigint  NOT NULL,
    created_date datetime(6) NOT NULL,
    id           bigint  NOT NULL AUTO_INCREMENT,
    student_id   bigint  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE comment
(
    deleted      bit     NOT NULL,
    like_count   integer NOT NULL,
    created_date datetime(6) NOT NULL,
    feed_id      bigint  NOT NULL,
    id           bigint  NOT NULL AUTO_INCREMENT,
    parent_id    bigint,
    student_id   bigint  NOT NULL,
    updated_date datetime(6) NOT NULL,
    content      TEXT    NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE comment_like
(
    comment_id   bigint NOT NULL,
    created_date datetime(6) NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    student_id   bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE expo_token
(
    id         bigint       NOT NULL AUTO_INCREMENT,
    student_id bigint       NOT NULL,
    token      varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE feed
(
    comment_count integer      NOT NULL,
    like_count    integer      NOT NULL,
    view_count    integer      NOT NULL,
    category_id   bigint       NOT NULL,
    created_date  datetime(6) NOT NULL,
    id            bigint       NOT NULL AUTO_INCREMENT,
    student_id    bigint       NOT NULL,
    university_id bigint       NOT NULL,
    updated_date  datetime(6) NOT NULL,
    content       TEXT         NOT NULL,
    title         varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE feed_image
(
    created_date datetime(6) NOT NULL,
    feed_id      bigint       NOT NULL,
    id           bigint       NOT NULL AUTO_INCREMENT,
    url          varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE feed_like
(
    created_date datetime(6) NOT NULL,
    feed_id      bigint NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    student_id   bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE interest
(
    id            bigint       NOT NULL AUTO_INCREMENT,
    interest_name varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE language
(
    id            bigint       NOT NULL AUTO_INCREMENT,
    language_name varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE major
(
    id         bigint       NOT NULL AUTO_INCREMENT,
    major_name varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE profile_image
(
    created_date datetime(6) NOT NULL,
    id           bigint       NOT NULL AUTO_INCREMENT,
    student_id   bigint       NOT NULL,
    url          varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE registered_phone_number
(
    authentication_code varchar(6)  NOT NULL,
    created_date        datetime(6) NOT NULL,
    id                  bigint      NOT NULL AUTO_INCREMENT,
    phone_number        varchar(11) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE report
(
    id               bigint NOT NULL AUTO_INCREMENT,
    reported_id      bigint NOT NULL,
    reported_user_id bigint NOT NULL,
    reporter_id      bigint NOT NULL,
    content          TEXT   NOT NULL,
    type             enum ('CHATROOM','COMMENT','FEED') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student
(
    certificated            bit DEFAULT false NOT NULL,
    korean                  bit               NOT NULL,
    created_date            datetime(6) NOT NULL,
    id                      bigint            NOT NULL AUTO_INCREMENT,
    university_id           bigint            NOT NULL,
    updated_date            datetime(6) NOT NULL,
    phone_number            varchar(11)       NOT NULL,
    country                 varchar(64)       NOT NULL,
    name                    varchar(64)       NOT NULL,
    character_profile_image varchar(255)      NOT NULL,
    email                   varchar(255),
    gender                  enum ('FEMALE','MALE','UNKNOWN') NOT NULL,
    role                    enum ('ADMIN','STUDENT') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student_email
(
    authentication_code varchar(4)   NOT NULL,
    created_date        datetime(6) NOT NULL,
    id                  bigint       NOT NULL AUTO_INCREMENT,
    email               varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student_id_card
(
    created_date datetime(6) NOT NULL,
    id           bigint       NOT NULL AUTO_INCREMENT,
    student_id   bigint       NOT NULL,
    image_url    varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student_interest
(
    created_date datetime(6) NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    interest_id  bigint NOT NULL,
    student_id   bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student_language
(
    created_date datetime(6) NOT NULL,
    id           bigint NOT NULL AUTO_INCREMENT,
    language_id  bigint NOT NULL,
    student_id   bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE student_major
(
    id         bigint NOT NULL AUTO_INCREMENT,
    major_id   bigint NOT NULL,
    student_id bigint NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE university
(
    id              bigint       NOT NULL AUTO_INCREMENT,
    university_name varchar(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
