package com.team.buddyya.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.team.buddyya.common.exception.CommonException;
import com.team.buddyya.common.exception.CommonExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3UploadService {

    private final AmazonS3Client amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.s3.url}")
    private String defaultUrl;

    public String uploadFile(String dir, MultipartFile file) {
        try {
            String bucketDir = bucketName + dir;
            String dirUrl = defaultUrl + dir + "/";
            String fileName = generateFileName(file);
            amazonS3.putObject(bucketDir, fileName, file.getInputStream(), getObjectMetadata(file));
            return dirUrl + fileName;
        } catch (IOException e) {
            throw new CommonException(CommonExceptionType.FILE_UPLOAD_ERROR);
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentDisposition("inline");
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    private String generateFileName(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        return fileName.replaceAll("[\\s,]", "_");
    }

    public void deleteFile(String dir, String fileUrl) {
        String fileName = extractFileName(fileUrl);
        amazonS3.deleteObject(bucketName + dir, fileName);
    }

    private String extractFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}
