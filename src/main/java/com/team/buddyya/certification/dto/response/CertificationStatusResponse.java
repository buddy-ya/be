package com.team.buddyya.certification.dto.response;

import com.team.buddyya.student.domain.Student;

public record CertificationStatusResponse(
        boolean isCertificated,
        boolean isStudentIdCardRequested,
        boolean isKorean
) {

    public static CertificationStatusResponse from(Student student, boolean isStudentIdCardRequested) {
        return new CertificationStatusResponse(
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean());
    }
}
