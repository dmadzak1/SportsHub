package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
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
}
