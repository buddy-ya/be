package com.team.buddyya.certification.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record SendStudentIdCardRequest(MultipartFile image) {
}
