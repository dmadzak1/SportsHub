package com.sportshub.analytics.repository;

import com.sportshub.analytics.model.RevenueLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueLogRepository extends JpaRepository<RevenueLog, Long> {
    List<RevenueLog> findByDate(LocalDate date);
    List<RevenueLog> findByStatisticsStatId(Long statId);

    @Query("""
       SELECT r
       FROM RevenueLog r
       WHERE r.date BETWEEN :fromDate AND :toDate
       ORDER BY r.date ASC
       """)

    List<RevenueLog> findRevenueLogsForPeriod(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
           SELECT r
           FROM RevenueLog r
           WHERE MONTH(r.date) = :month
           AND YEAR(r.date) = :year
           """)
    List<RevenueLog> findByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year
    );
}