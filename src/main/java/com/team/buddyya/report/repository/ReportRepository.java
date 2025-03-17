package com.team.buddyya.report.repository;

import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByType(ReportType type);
}
