package com.sportshub.analytics.service;

import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.repository.ReportRepository;
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
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private Report report;

    @BeforeEach
    void setUp() {
        report = new Report("REVENUE");
        report.setReportId(1L);
    }

    @Test
    void getAll_returnsAllReports() {
        when(reportRepository.findAll()).thenReturn(List.of(report));

        List<Report> result = reportService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReportType()).isEqualTo("REVENUE");
    }

    @Test
    void getById_existingId_returnsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        Report result = reportService.getById(1L);

        assertThat(result.getReportType()).isEqualTo("REVENUE");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByType_returnsFilteredReports() {
        when(reportRepository.findByReportType("REVENUE")).thenReturn(List.of(report));

        List<Report> result = reportService.getByType("revenue");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReportType()).isEqualTo("REVENUE");
    }

    @Test
    void create_savesReport() {
        when(reportRepository.save(report)).thenReturn(report);

        Report result = reportService.create(report);

        assertThat(result.getReportType()).isEqualTo("REVENUE");
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void update_existingId_updatesReportType() {
        Report updated = new Report("CAPACITY");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Report result = reportService.update(1L, updated);

        assertThat(result.getReportType()).isEqualTo("CAPACITY");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.delete(1L);

        verify(reportRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(reportRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reportService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
