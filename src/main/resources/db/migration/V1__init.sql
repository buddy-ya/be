CREATE TABLE `auth_token`
(
    `created_date`  datetime(6) NOT NULL,
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `student_id`    bigint       NOT NULL,
    `refresh_token` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_t488atgt0g1qsucjmwxiy0uhm` (`student_id`),
    UNIQUE KEY `UK_4ykcglo3adb5kma4wjp2xsox8` (`refresh_token`),
    CONSTRAINT `FKse0wuw03sxpgoduvv80v8xgf9` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `avatar`
(
    `active`               bit(1) NOT NULL,
    `logged_out`           bit(1) NOT NULL,
    `notification_enabled` bit(1) NOT NULL,
    `created_date`         datetime(6) NOT NULL,
    `id`                   bigint NOT NULL AUTO_INCREMENT,
    `student_id`           bigint NOT NULL,
    `updated_date`         datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_oqwewcy88n5gdu8w8lipi9d06` (`student_id`),
    CONSTRAINT `FKm6mgi7ty5gienen5rege7rc22` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `block`
(
    `blocked_student_id` bigint NOT NULL,
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `student_id`         bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY                  `FKljbor00jub15sf4o2qya2n9jj` (`student_id`),
    CONSTRAINT `FKljbor00jub15sf4o2qya2n9jj` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `bookmark`
(
    `created_date` datetime(6) NOT NULL,
    `feed_id`      bigint NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKon0uigm2gw7i2cgcgg82mxj1d` (`feed_id`),
    KEY            `FKkib9p9kren79qy3ucbup6mb2` (`student_id`),
    CONSTRAINT `FKkib9p9kren79qy3ucbup6mb2` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKon0uigm2gw7i2cgcgg82mxj1d` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `category`
(
    `id`   bigint      NOT NULL AUTO_INCREMENT,
    `name` varchar(15) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chat`
(
    `chatroom_id`  bigint NOT NULL,
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `student_id`   bigint NOT NULL,
    `message`      text   NOT NULL,
    `type`         enum('IMAGE','TALK') NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKt1s17j5bqqa0kmqosbw83fhn7` (`chatroom_id`),
    KEY            `FKaso88nqyvhpu1w9ck0aen31bf` (`student_id`),
    CONSTRAINT `FKaso88nqyvhpu1w9ck0aen31bf` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKt1s17j5bqqa0kmqosbw83fhn7` FOREIGN KEY (`chatroom_id`) REFERENCES `chatroom` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chat_request`
(
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `receiver_id`  bigint NOT NULL,
    `sender_id`    bigint NOT NULL,
    `updated_date` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKq8pjrg277g5rw3ix53l2urjms` (`receiver_id`),
    KEY            `FKhuslhbfxgbyasvnvwtwj7m5os` (`sender_id`),
    CONSTRAINT `FKhuslhbfxgbyasvnvwtwj7m5os` FOREIGN KEY (`sender_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKq8pjrg277g5rw3ix53l2urjms` FOREIGN KEY (`receiver_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chatroom`
(
    `created_date`      datetime(6) NOT NULL,
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `last_message_time` datetime(6) NOT NULL,
    `last_message`      varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `chatroom_student`
(
    `exited`       bit(1) NOT NULL,
    `unread_count` int    NOT NULL,
    `chatroom_id`  bigint NOT NULL,
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKcialbtp1c02s51uss4uodp8os` (`chatroom_id`),
    KEY            `FK76xpl7u7yojc0okgy0nbb02uw` (`student_id`),
    CONSTRAINT `FK76xpl7u7yojc0okgy0nbb02uw` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKcialbtp1c02s51uss4uodp8os` FOREIGN KEY (`chatroom_id`) REFERENCES `chatroom` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `comment`
(
    `deleted`      bit(1) NOT NULL,
    `like_count`   int    NOT NULL,
    `created_date` datetime(6) NOT NULL,
    `feed_id`      bigint NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `parent_id`    bigint DEFAULT NULL,
    `student_id`   bigint NOT NULL,
    `updated_date` datetime(6) NOT NULL,
    `content`      text   NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKmq57ocw5jrw8rd2lot1g8t0v2` (`feed_id`),
    KEY            `FKde3rfu96lep00br5ov0mdieyt` (`parent_id`),
    KEY            `FKamc40esh2mg78arkc68uflpjs` (`student_id`),
    CONSTRAINT `FKamc40esh2mg78arkc68uflpjs` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKde3rfu96lep00br5ov0mdieyt` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`),
    CONSTRAINT `FKmq57ocw5jrw8rd2lot1g8t0v2` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `comment_like`
(
    `comment_id`   bigint NOT NULL,
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKqlv8phl1ibeh0efv4dbn3720p` (`comment_id`),
    KEY            `FKmrtgw0h2g2glknaf9ei9v7fex` (`student_id`),
    CONSTRAINT `FKmrtgw0h2g2glknaf9ei9v7fex` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKqlv8phl1ibeh0efv4dbn3720p` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `expo_token`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `student_id` bigint       NOT NULL,
    `token`      varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_nau15khiit86qsp5k8s2akdos` (`student_id`),
    CONSTRAINT `FKixfisn08bglag4vr67ijy5oga` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed`
(
    `comment_count` int          NOT NULL,
    `like_count`    int          NOT NULL,
    `view_count`    int          NOT NULL,
    `category_id`   bigint       NOT NULL,
    `created_date`  datetime(6) NOT NULL,
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `student_id`    bigint       NOT NULL,
    `university_id` bigint       NOT NULL,
    `updated_date`  datetime(6) NOT NULL,
    `content`       text         NOT NULL,
    `title`         varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY             `FKqrkghx9e7x9qr0boho5n3ein5` (`category_id`),
    KEY             `FKb3bbh7n8idicr9x735n1q2tua` (`student_id`),
    KEY             `FKf8krv1sjp46o2lgluoq1x4xbe` (`university_id`),
    CONSTRAINT `FKb3bbh7n8idicr9x735n1q2tua` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKf8krv1sjp46o2lgluoq1x4xbe` FOREIGN KEY (`university_id`) REFERENCES `university` (`id`),
    CONSTRAINT `FKqrkghx9e7x9qr0boho5n3ein5` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed_image`
(
    `created_date` datetime(6) NOT NULL,
    `feed_id`      bigint       NOT NULL,
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `url`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FK6ucsld0tx762qhq8sp5khgv3n` (`feed_id`),
    CONSTRAINT `FK6ucsld0tx762qhq8sp5khgv3n` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `feed_like`
(
    `created_date` datetime(6) NOT NULL,
    `feed_id`      bigint NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKgurobtyio3jh1vn4n8tmqt842` (`feed_id`),
    KEY            `FK99t4rltopkpgs92w9k9u91bb5` (`student_id`),
    CONSTRAINT `FK99t4rltopkpgs92w9k9u91bb5` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKgurobtyio3jh1vn4n8tmqt842` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `flyway_schema_history`
(
    `installed_rank` int           NOT NULL,
    `version`        varchar(50)            DEFAULT NULL,
    `description`    varchar(200)  NOT NULL,
    `type`           varchar(20)   NOT NULL,
    `script`         varchar(1000) NOT NULL,
    `checksum`       int                    DEFAULT NULL,
    `installed_by`   varchar(100)  NOT NULL,
    `installed_on`   timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `execution_time` int           NOT NULL,
    `success`        tinyint(1) NOT NULL,
    PRIMARY KEY (`installed_rank`),
    KEY              `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB;

CREATE TABLE `interest`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `interest_name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_6xcnbms9b3imfjjbw0ewd2wgj` (`interest_name`)
) ENGINE=InnoDB;

CREATE TABLE `language`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `language_name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_mpvpyjgetru6cvudxld43ek8p` (`language_name`)
) ENGINE=InnoDB;

CREATE TABLE `major`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `major_name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_k7byeihdppur4v990tgejyogi` (`major_name`)
) ENGINE=InnoDB;

CREATE TABLE `profile_image`
(
    `created_date` datetime(6) NOT NULL,
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `student_id`   bigint       NOT NULL,
    `url`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_o5qt6p73dci5kpkry9q3yp2j9` (`student_id`),
    CONSTRAINT `FKdins07owbjgivh7fvplcoek3p` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `registered_phone_number`
(
    `authentication_code` varchar(6)  NOT NULL,
    `created_date`        datetime(6) NOT NULL,
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `phone_number`        varchar(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_689p3ai3dbo8ql9od3ldvviag` (`authentication_code`),
    UNIQUE KEY `UK_1v9jv7yx1s8x8h1iso2o53imi` (`phone_number`)
) ENGINE=InnoDB;

CREATE TABLE `report`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `report_user_id`   bigint NOT NULL,
    `reported_id`      bigint NOT NULL,
    `reported_user_id` bigint NOT NULL,
    `content`          text   NOT NULL,
    `type`             enum('CHATROOM','COMMENT','FEED') NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student`
(
    `certificated`            bit(1)       NOT NULL DEFAULT b'0',
    `korean`                  bit(1)       NOT NULL,
    `created_date`            datetime(6) NOT NULL,
    `id`                      bigint       NOT NULL AUTO_INCREMENT,
    `university_id`           bigint       NOT NULL,
    `updated_date`            datetime(6) NOT NULL,
    `phone_number`            varchar(11)  NOT NULL,
    `country`                 varchar(64)  NOT NULL,
    `name`                    varchar(64)  NOT NULL,
    `character_profile_image` varchar(255) NOT NULL,
    `email`                   varchar(255)          DEFAULT NULL,
    `gender`                  enum('FEMALE','MALE','UNKNOWN') NOT NULL,
    `role`                    enum('ADMIN','STUDENT') NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_i3xrfnuv2icsd1vhvn6c108ec` (`phone_number`),
    UNIQUE KEY `UK_fe0i52si7ybu0wjedj6motiim` (`email`),
    KEY                       `FK157t7rer269uuhfphq1mcd7y9` (`university_id`),
    CONSTRAINT `FK157t7rer269uuhfphq1mcd7y9` FOREIGN KEY (`university_id`) REFERENCES `university` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_email`
(
    `authentication_code` varchar(4)   NOT NULL,
    `created_date`        datetime(6) NOT NULL,
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `email`               varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_stpkh8gcfcndsb9klqiweul6s` (`email`)
) ENGINE=InnoDB;

CREATE TABLE `student_id_card`
(
    `created_date` datetime(6) NOT NULL,
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `student_id`   bigint       NOT NULL,
    `image_url`    varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_n8k07xf7m2c0v0pd509ghc1qa` (`student_id`),
    CONSTRAINT `FK9m5s8ryocr6e1isb547t2ggsy` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_interest`
(
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `interest_id`  bigint NOT NULL,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKq9mmuxwmy5o4sedje8q81ract` (`interest_id`),
    KEY            `FKfjmgepv18vbrg270058y15fii` (`student_id`),
    CONSTRAINT `FKfjmgepv18vbrg270058y15fii` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKq9mmuxwmy5o4sedje8q81ract` FOREIGN KEY (`interest_id`) REFERENCES `interest` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_language`
(
    `created_date` datetime(6) NOT NULL,
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `language_id`  bigint NOT NULL,
    `student_id`   bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY            `FK15ewtpkp3uagt4ea6h2574t96` (`language_id`),
    KEY            `FKcioacp2rv5t8jojmayi84dhoo` (`student_id`),
    CONSTRAINT `FK15ewtpkp3uagt4ea6h2574t96` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
    CONSTRAINT `FKcioacp2rv5t8jojmayi84dhoo` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `student_major`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `major_id`   bigint NOT NULL,
    `student_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY          `FKno88xq2me929n9me011hj11x9` (`major_id`),
    KEY          `FK4xn4k5x0q51sit66fkqh68qwq` (`student_id`),
    CONSTRAINT `FK4xn4k5x0q51sit66fkqh68qwq` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
    CONSTRAINT `FKno88xq2me929n9me011hj11x9` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `university`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT,
    `university_name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_f4eogdcmmwr1jl80i3e71d2vv` (`university_name`)
) ENGINE=InnoDB;
