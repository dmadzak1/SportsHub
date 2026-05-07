package com.sportshub.analytics.controller;

import com.sportshub.analytics.dto.ReportDTO;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.service.ReportService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final ModelMapper modelMapper;

    public ReportController(ReportService reportService, ModelMapper modelMapper) {
        this.reportService = reportService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<ReportDTO> getAll() {
        return reportService.getAll().stream()
                .map(r -> modelMapper.map(r, ReportDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    public ReportDTO getById(@PathVariable Long id) {
        return modelMapper.map(reportService.getById(id), ReportDTO.class);
    }

    @GetMapping("/type/{type}")
    public List<ReportDTO> getByType(@PathVariable String type) {
        return reportService.getByType(type).stream()
                .map(r -> modelMapper.map(r, ReportDTO.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReportDTO> create(@Valid @RequestBody ReportDTO dto) {
        Report created = reportService.create(new Report(dto.getReportType()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, ReportDTO.class));
    }

    @PutMapping("/{id}")
    public ReportDTO update(@PathVariable Long id, @Valid @RequestBody ReportDTO dto) {
        Report updated = reportService.update(id, new Report(dto.getReportType()));
        return modelMapper.map(updated, ReportDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
