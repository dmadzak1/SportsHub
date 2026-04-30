package com.sportshub.analytics;

import com.sportshub.analytics.model.*;
import com.sportshub.analytics.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final ReportRepository reportRepository;
    private final StatisticsRepository statisticsRepository;
    private final RevenueLogRepository revenueLogRepository;

    public DataLoader(ReportRepository reportRepository,
                      StatisticsRepository statisticsRepository,
                      RevenueLogRepository revenueLogRepository) {
        this.reportRepository = reportRepository;
        this.statisticsRepository = statisticsRepository;
        this.revenueLogRepository = revenueLogRepository;
    }

    @Override
    public void run(String... args) {
        Report revenueReport   = reportRepository.save(new Report("REVENUE"));
        Report capacityReport  = reportRepository.save(new Report("CAPACITY"));

        Statistics totalRevenue     = statisticsRepository.save(new Statistics(revenueReport, "TOTAL_REVENUE", 15000.0));
        Statistics totalReservations = statisticsRepository.save(new Statistics(revenueReport, "TOTAL_RESERVATIONS", 320.0));
        Statistics occupancyRate    = statisticsRepository.save(new Statistics(capacityReport, "OCCUPANCY_RATE", 78.5));

        revenueLogRepository.save(new RevenueLog(totalRevenue, LocalDate.now().minusDays(2), 4500.0));
        revenueLogRepository.save(new RevenueLog(totalRevenue, LocalDate.now().minusDays(1), 5200.0));
        revenueLogRepository.save(new RevenueLog(totalRevenue, LocalDate.now(),              5300.0));
        revenueLogRepository.save(new RevenueLog(occupancyRate, LocalDate.now(),             78.5));

        System.out.println("✅ Analytics Service: testni podaci uspješno učitani.");
    }
}
