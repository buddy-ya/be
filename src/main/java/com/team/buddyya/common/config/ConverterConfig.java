package com.team.buddyya.common.config;

import com.team.buddyya.common.service.EncryptedConverter;
import com.team.buddyya.common.service.EncryptionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Autowired
    private EncryptionService encryptionService;

    @PostConstruct
    public void init() {
        EncryptedConverter.setEncryptionService(encryptionService);
    }
}
