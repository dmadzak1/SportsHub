package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.RevenueLog;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.repository.ReportRepository;
import com.sportshub.analytics.repository.RevenueLogRepository;
import com.sportshub.analytics.repository.StatisticsRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final RevenueLogRepository revenueLogRepository;
    private final StatisticsRepository statisticsRepository;

    public ReportService(
            ReportRepository reportRepository,
            RevenueLogRepository revenueLogRepository,
            StatisticsRepository statisticsRepository
    ) {
        this.reportRepository = reportRepository;
        this.revenueLogRepository = revenueLogRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public Report getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));
    }

    public List<Report> getByType(String type) {
        return reportRepository.findByReportType(type.toUpperCase());
    }

    public Report create(Report report) {
        return reportRepository.save(report);
    }

    public Report update(Long id, Report updated) {
        Report existing = getById(id);
        existing.setReportType(updated.getReportType());
        return reportRepository.save(existing);
    }

    public void delete(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report", id);
        }
        reportRepository.deleteById(id);
    }

    public Page<Report> getAllPaged(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Transactional
    public Report generateMonthlyReport(int month, int year) {
        List<RevenueLog> logs = revenueLogRepository.findByMonthAndYear(month, year);

        double totalRevenue = logs.stream()
                .mapToDouble(RevenueLog::getAmount)
                .sum();

        Report report = new Report("REVENUE");
        Report savedReport = reportRepository.save(report);

        Statistics statistics = new Statistics(
                savedReport,
                "MONTHLY_REVENUE_" + month + "_" + year,
                totalRevenue
        );

        statisticsRepository.save(statistics);

        return savedReport;
    }
}