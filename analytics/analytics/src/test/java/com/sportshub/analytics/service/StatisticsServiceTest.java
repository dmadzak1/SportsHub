package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.repository.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private Report report;
    private Statistics stat;

    @BeforeEach
    void setUp() {
        report = new Report("REVENUE");
        report.setReportId(1L);
        stat = new Statistics(report, "TOTAL_REVENUE", 1000.0);
        stat.setStatId(1L);
    }

    @Test
    void getAll_returnsAllStatistics() {
        when(statisticsRepository.findAll()).thenReturn(List.of(stat));

        List<Statistics> result = statisticsService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMetric()).isEqualTo("TOTAL_REVENUE");
    }

    @Test
    void getById_existingId_returnsStatistics() {
        when(statisticsRepository.findById(1L)).thenReturn(Optional.of(stat));

        Statistics result = statisticsService.getById(1L);

        assertThat(result.getValue()).isEqualTo(1000.0);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(statisticsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statisticsService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByReport_returnsFilteredStatistics() {
        when(statisticsRepository.findByReportReportId(1L)).thenReturn(List.of(stat));

        List<Statistics> result = statisticsService.getByReport(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMetric()).isEqualTo("TOTAL_REVENUE");
    }

    @Test
    void getByMetric_returnsFilteredStatistics() {
        when(statisticsRepository.findByMetric("TOTAL_REVENUE")).thenReturn(List.of(stat));

        List<Statistics> result = statisticsService.getByMetric("total_revenue");

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesStatistics() {
        when(statisticsRepository.save(stat)).thenReturn(stat);

        Statistics result = statisticsService.create(stat);

        assertThat(result.getMetric()).isEqualTo("TOTAL_REVENUE");
        verify(statisticsRepository, times(1)).save(stat);
    }

    @Test
    void update_existingId_updatesFields() {
        Statistics updated = new Statistics();
        updated.setMetric("OCCUPANCY_RATE");
        updated.setValue(75.5);
        when(statisticsRepository.findById(1L)).thenReturn(Optional.of(stat));
        when(statisticsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Statistics result = statisticsService.update(1L, updated);

        assertThat(result.getMetric()).isEqualTo("OCCUPANCY_RATE");
        assertThat(result.getValue()).isEqualTo(75.5);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(statisticsRepository.existsById(1L)).thenReturn(true);

        statisticsService.delete(1L);

        verify(statisticsRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(statisticsRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> statisticsService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
