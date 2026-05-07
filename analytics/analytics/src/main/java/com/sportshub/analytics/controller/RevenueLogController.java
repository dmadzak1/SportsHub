package com.sportshub.analytics.controller;

import com.sportshub.analytics.dto.RevenueLogDTO;
import com.sportshub.analytics.model.RevenueLog;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.service.RevenueLogService;
import com.sportshub.analytics.service.StatisticsService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/revenue-logs")
public class RevenueLogController {

    private final RevenueLogService revenueLogService;
    private final StatisticsService statisticsService;
    private final ModelMapper modelMapper;

    public RevenueLogController(RevenueLogService revenueLogService,
                                StatisticsService statisticsService,
                                ModelMapper modelMapper) {
        this.revenueLogService = revenueLogService;
        this.statisticsService = statisticsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<RevenueLogDTO> getAll() {
        return revenueLogService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public RevenueLogDTO getById(@PathVariable Long id) {
        return toDTO(revenueLogService.getById(id));
    }

    @GetMapping("/date/{date}")
    public List<RevenueLogDTO> getByDate(@PathVariable String date) {
        return revenueLogService.getByDate(LocalDate.parse(date)).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/statistics/{statId}")
    public List<RevenueLogDTO> getByStatistics(@PathVariable Long statId) {
        return revenueLogService.getByStatistics(statId).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<RevenueLogDTO> create(@Valid @RequestBody RevenueLogDTO dto) {
        Statistics statistics = statisticsService.getById(dto.getStatId());
        RevenueLog revenueLog = new RevenueLog(statistics, dto.getDate(), dto.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(revenueLogService.create(revenueLog)));
    }

    @PutMapping("/{id}")
    public RevenueLogDTO update(@PathVariable Long id, @Valid @RequestBody RevenueLogDTO dto) {
        RevenueLog partial = new RevenueLog();
        partial.setDate(dto.getDate());
        partial.setAmount(dto.getAmount());
        return toDTO(revenueLogService.update(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        revenueLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RevenueLogDTO toDTO(RevenueLog log) {
        RevenueLogDTO dto = new RevenueLogDTO();
        dto.setRevenueId(log.getRevenueId());
        dto.setStatId(log.getStatistics().getStatId());
        dto.setDate(log.getDate());
        dto.setAmount(log.getAmount());
        return dto;
    }
}
