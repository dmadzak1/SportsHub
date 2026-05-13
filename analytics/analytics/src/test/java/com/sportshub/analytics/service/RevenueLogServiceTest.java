package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.RevenueLog;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.repository.RevenueLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueLogServiceTest {

    @Mock
    private RevenueLogRepository revenueLogRepository;

    @InjectMocks
    private RevenueLogService revenueLogService;

    private Statistics stat;
    private RevenueLog log;
    private final LocalDate testDate = LocalDate.of(2025, 6, 1);

    @BeforeEach
    void setUp() {
        Report report = new Report("REVENUE");
        report.setReportId(1L);
        stat = new Statistics(report, "TOTAL_REVENUE", 1000.0);
        stat.setStatId(1L);
        log = new RevenueLog(stat, testDate, 500.0);
        log.setRevenueId(1L);
    }

    @Test
    void getAll_returnsAllRevenueLogs() {
        when(revenueLogRepository.findAll()).thenReturn(List.of(log));

        List<RevenueLog> result = revenueLogService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(500.0);
    }

    @Test
    void getById_existingId_returnsRevenueLog() {
        when(revenueLogRepository.findById(1L)).thenReturn(Optional.of(log));

        RevenueLog result = revenueLogService.getById(1L);

        assertThat(result.getAmount()).isEqualTo(500.0);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(revenueLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> revenueLogService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByDate_returnsFilteredLogs() {
        when(revenueLogRepository.findByDate(testDate)).thenReturn(List.of(log));

        List<RevenueLog> result = revenueLogService.getByDate(testDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDate()).isEqualTo(testDate);
    }

    @Test
    void getByStatistics_returnsFilteredLogs() {
        when(revenueLogRepository.findByStatisticsStatId(1L)).thenReturn(List.of(log));

        List<RevenueLog> result = revenueLogService.getByStatistics(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesRevenueLog() {
        when(revenueLogRepository.save(log)).thenReturn(log);

        RevenueLog result = revenueLogService.create(log);

        assertThat(result.getAmount()).isEqualTo(500.0);
        verify(revenueLogRepository, times(1)).save(log);
    }

    @Test
    void update_existingId_updatesFields() {
        RevenueLog updated = new RevenueLog();
        updated.setDate(LocalDate.of(2025, 7, 1));
        updated.setAmount(750.0);
        when(revenueLogRepository.findById(1L)).thenReturn(Optional.of(log));
        when(revenueLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RevenueLog result = revenueLogService.update(1L, updated);

        assertThat(result.getAmount()).isEqualTo(750.0);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(revenueLogRepository.existsById(1L)).thenReturn(true);

        revenueLogService.delete(1L);

        verify(revenueLogRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(revenueLogRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> revenueLogService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
