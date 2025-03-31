package com.team.buddyya.common.service;

import com.team.buddyya.common.exception.CommonException;
import com.team.buddyya.common.exception.CommonExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${encryption.aes-key}")
    private String secretKey;

    private String ALGORITHM = "AES";

    public String encrypt(String plainText) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CommonException(CommonExceptionType.ENCRYPTION_ERROR);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);
        } catch (Exception e) {
            throw new CommonException(CommonExceptionType.ENCRYPTION_ERROR);
        }
    }
}
