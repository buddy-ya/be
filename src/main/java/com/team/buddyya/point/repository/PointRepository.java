package com.team.buddyya.point.repository;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByStudent(Student student);
}