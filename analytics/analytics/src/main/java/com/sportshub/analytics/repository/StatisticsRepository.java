package com.sportshub.analytics.repository;

import com.sportshub.analytics.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findByReportReportId(Long reportId);
    List<Statistics> findByMetric(String metric);
}
