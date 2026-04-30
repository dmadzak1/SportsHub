package com.sportshub.analytics.repository;

import com.sportshub.analytics.model.RevenueLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueLogRepository extends JpaRepository<RevenueLog, Long> {
    List<RevenueLog> findByDate(LocalDate date);
    List<RevenueLog> findByStatisticsStatId(Long statId);
}