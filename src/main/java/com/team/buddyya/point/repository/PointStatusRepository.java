package com.team.buddyya.point.repository;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointChangeType;
import com.team.buddyya.point.domain.PointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointStatusRepository extends JpaRepository<PointStatus, Long> {

    List<PointStatus> findAllByPointOrderByCreatedDateDesc(Point point);

    void deleteAllByPoint(Point point);
}
