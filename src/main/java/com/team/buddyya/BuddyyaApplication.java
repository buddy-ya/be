package com.team.buddyya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BuddyyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuddyyaApplication.class, args);
    }

}
