package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.RevenueLog;
import com.sportshub.analytics.repository.RevenueLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueLogService {

    private final RevenueLogRepository revenueLogRepository;

    public RevenueLogService(RevenueLogRepository revenueLogRepository) {
        this.revenueLogRepository = revenueLogRepository;
    }

    public List<RevenueLog> getAll() {
        return revenueLogRepository.findAll();
    }

    public RevenueLog getById(Long id) {
        return revenueLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RevenueLog", id));
    }

    public List<RevenueLog> getByDate(LocalDate date) {
        return revenueLogRepository.findByDate(date);
    }

    public List<RevenueLog> getByStatistics(Long statId) {
        return revenueLogRepository.findByStatisticsStatId(statId);
    }

    public RevenueLog create(RevenueLog revenueLog) {
        return revenueLogRepository.save(revenueLog);
    }

    public RevenueLog update(Long id, RevenueLog updated) {
        RevenueLog existing = getById(id);
        existing.setDate(updated.getDate());
        existing.setAmount(updated.getAmount());
        return revenueLogRepository.save(existing);
    }

    public void delete(Long id) {
        if (!revenueLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("RevenueLog", id);
        }
        revenueLogRepository.deleteById(id);
    }
}
