package com.team.buddyya.point.service;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.exception.PointException;
import com.team.buddyya.point.exception.PointExceptionType;
import com.team.buddyya.point.repository.PointRepository;
import com.team.buddyya.student.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FindPointService {

    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public Point findByStudent(Student student){
        return pointRepository.findByStudent(student)
                .orElseThrow(()-> new PointException(PointExceptionType.POINT_NOT_FOUND));
    }
}
