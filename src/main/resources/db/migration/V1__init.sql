CREATE TABLE `university`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `university_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_university_name` (`university_name`)
) ENGINE=InnoDB;

CREATE TABLE `student`
(
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT,
    `university_id`           BIGINT       NOT NULL,
    `created_date`            DATETIME(6) NOT NULL,
    `updated_date`            DATETIME(6) NOT NULL,
    `phone_number`            VARCHAR(11)  NOT NULL UNIQUE,
    `country`                 VARCHAR(64)  NOT NULL,
    `name`                    VARCHAR(64)  NOT NULL,
    `character_profile_image` VARCHAR(255) NOT NULL,
    `email`                   VARCHAR(255)          DEFAULT NULL UNIQUE,
    `certificated`            BIT(1)       NOT NULL DEFAULT b'0',
    `korean`                  BIT(1)       NOT NULL,
    `gender`                  ENUM('FEMALE', 'MALE', 'UNKNOWN') NOT NULL,
    `role`                    ENUM('ADMIN', 'STUDENT') NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`university_id`) REFERENCES `university` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `auth_token`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `student_id`    BIGINT       NOT NULL,
    `refresh_token` VARCHAR(255) NOT NULL UNIQUE,
    `created_date`  DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_auth_token_student_id` (`student_id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `avatar`
(
    `id`                   BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`           BIGINT NOT NULL UNIQUE,
    `active`               BIT(1) NOT NULL,
    `logged_out`           BIT(1) NOT NULL,
    `notification_enabled` BIT(1) NOT NULL,
    `created_date`         DATETIME(6) NOT NULL,
    `updated_date`         DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `block`
(
    `id`                 BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`         BIGINT NOT NULL,
    `blocked_student_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `category`
(
    `id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(15) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chatroom`
(
    `id`                BIGINT NOT NULL AUTO_INCREMENT,
    `created_date`      DATETIME(6) NOT NULL,
    `last_message_time` DATETIME(6) NOT NULL,
    `last_message`      VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chatroom_student`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `chatroom_id`  BIGINT NOT NULL,
    `student_id`   BIGINT NOT NULL,
    `exited`       BIT(1) NOT NULL,
    `unread_count` INT    NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`chatroom_id`) REFERENCES `chatroom` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chat`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `chatroom_id`  BIGINT NOT NULL,
    `student_id`   BIGINT NOT NULL,
    `message`      TEXT   NOT NULL,
    `type`         ENUM('IMAGE', 'TALK') NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`chatroom_id`) REFERENCES `chatroom` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chat_request`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `receiver_id`  BIGINT NOT NULL,
    `sender_id`    BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    `updated_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`receiver_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`sender_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `student_id`    BIGINT       NOT NULL,
    `university_id` BIGINT       NOT NULL,
    `category_id`   BIGINT       NOT NULL,
    `title`         VARCHAR(255) NOT NULL,
    `content`       TEXT         NOT NULL,
    `comment_count` INT          NOT NULL,
    `like_count`    INT          NOT NULL,
    `view_count`    INT          NOT NULL,
    `created_date`  DATETIME(6) NOT NULL,
    `updated_date`  DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`university_id`) REFERENCES `university` (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `comment`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT NOT NULL,
    `feed_id`      BIGINT NOT NULL,
    `parent_id`    BIGINT DEFAULT NULL,
    `content`      TEXT   NOT NULL,
    `deleted`      BIT(1) NOT NULL,
    `like_count`   INT    NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    `updated_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`),
    FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `comment_like`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `comment_id`   BIGINT NOT NULL,
    `student_id`   BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `expo_token`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT       NOT NULL UNIQUE,
    `token`      VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed_image`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `feed_id`      BIGINT       NOT NULL,
    `url`          VARCHAR(255) NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed_like`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT NOT NULL,
    `feed_id`      BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `bookmark`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT NOT NULL,
    `feed_id`      BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `interest`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `interest_name` VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_interest`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT NOT NULL,
    `interest_id`  BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `language`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `language_name` VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_language`
(
    `id`           BIGINT NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT NOT NULL,
    `language_id`  BIGINT NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `major`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `major_name` VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_major`
(
    `id`         BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `major_id`   BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    FOREIGN KEY (`major_id`) REFERENCES `major` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `profile_image`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT       NOT NULL UNIQUE,
    `url`          VARCHAR(255) NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `registered_phone_number`
(
    `id`                  BIGINT      NOT NULL AUTO_INCREMENT,
    `phone_number`        VARCHAR(11) NOT NULL UNIQUE,
    `authentication_code` VARCHAR(6)  NOT NULL UNIQUE,
    `created_date`        DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `report`
(
    `id`               BIGINT NOT NULL AUTO_INCREMENT,
    `report_user_id`   BIGINT NOT NULL,
    `reported_id`      BIGINT NOT NULL,
    `reported_user_id` BIGINT NOT NULL,
    `content`          TEXT   NOT NULL,
    `type`             ENUM('CHATROOM', 'COMMENT', 'FEED') NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_email`
(
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT,
    `email`               VARCHAR(255) NOT NULL UNIQUE,
    `authentication_code` VARCHAR(4)   NOT NULL,
    `created_date`        DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_id_card`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `student_id`   BIGINT       NOT NULL UNIQUE,
    `image_url`    VARCHAR(255) NOT NULL,
    `created_date` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;
