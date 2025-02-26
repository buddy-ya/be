package com.team.buddyya.report.repository;

import com.team.buddyya.report.domain.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

    List<ReportImage> findByReportId(Long reportId);
}
