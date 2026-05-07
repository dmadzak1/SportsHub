package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public List<Statistics> getAll() {
        return statisticsRepository.findAll();
    }

    public Statistics getById(Long id) {
        return statisticsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statistics", id));
    }

    public List<Statistics> getByReport(Long reportId) {
        return statisticsRepository.findByReportReportId(reportId);
    }

    public List<Statistics> getByMetric(String metric) {
        return statisticsRepository.findByMetric(metric.toUpperCase());
    }

    public Statistics create(Statistics statistics) {
        return statisticsRepository.save(statistics);
    }

    public Statistics update(Long id, Statistics updated) {
        Statistics existing = getById(id);
        existing.setMetric(updated.getMetric());
        existing.setValue(updated.getValue());
        return statisticsRepository.save(existing);
    }

    public void delete(Long id) {
        if (!statisticsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Statistics", id);
        }
        statisticsRepository.deleteById(id);
    }
}
