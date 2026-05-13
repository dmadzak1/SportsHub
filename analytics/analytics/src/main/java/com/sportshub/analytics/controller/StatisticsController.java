package com.sportshub.analytics.controller;

import com.sportshub.analytics.dto.StatisticsDTO;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.service.ReportService;
import com.sportshub.analytics.service.StatisticsService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final ReportService reportService;
    private final ModelMapper modelMapper;

    public StatisticsController(StatisticsService statisticsService,
                                ReportService reportService,
                                ModelMapper modelMapper) {
        this.statisticsService = statisticsService;
        this.reportService = reportService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<StatisticsDTO> getAll() {
        return statisticsService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public StatisticsDTO getById(@PathVariable Long id) {
        return toDTO(statisticsService.getById(id));
    }

    @GetMapping("/report/{reportId}")
    public List<StatisticsDTO> getByReport(@PathVariable Long reportId) {
        return statisticsService.getByReport(reportId).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/metric/{metric}")
    public List<StatisticsDTO> getByMetric(@PathVariable String metric) {
        return statisticsService.getByMetric(metric).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<StatisticsDTO> create(@Valid @RequestBody StatisticsDTO dto) {
        Report report = reportService.getById(dto.getReportId());
        Statistics statistics = new Statistics(report, dto.getMetric(), dto.getValue());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(statisticsService.create(statistics)));
    }

    @PutMapping("/{id}")
    public StatisticsDTO update(@PathVariable Long id, @Valid @RequestBody StatisticsDTO dto) {
        Statistics partial = new Statistics();
        partial.setMetric(dto.getMetric());
        partial.setValue(dto.getValue());
        return toDTO(statisticsService.update(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statisticsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private StatisticsDTO toDTO(Statistics stat) {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setStatId(stat.getStatId());
        dto.setReportId(stat.getReport().getReportId());
        dto.setMetric(stat.getMetric());
        dto.setValue(stat.getValue());
        return dto;
    }
}
