package com.sportshub.facility.controller;

import com.sportshub.facility.dto.FacilityBatchDTO;
import com.sportshub.facility.dto.FacilityDTO;
import com.sportshub.facility.dto.PageResponseDTO;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.service.FacilityService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
public class FacilityController {

    private final FacilityService facilityService;
    private final ModelMapper modelMapper;

    public FacilityController(FacilityService facilityService, ModelMapper modelMapper) {
        this.facilityService = facilityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<FacilityDTO> getAll() {
        return facilityService.getAll().stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();
    }

    @GetMapping("/paginated")
    public PageResponseDTO<FacilityDTO> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "facilityId") String sortBy) {

        Page<Facility> result = facilityService.getPaginated(page, size, sortBy);
        List<FacilityDTO> content = result.getContent().stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();
        return new PageResponseDTO<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @GetMapping("/{id}")
    public FacilityDTO getById(@PathVariable Long id) {
        return modelMapper.map(facilityService.getById(id), FacilityDTO.class);
    }

    @GetMapping("/type/{type}")
    public List<FacilityDTO> getByType(@PathVariable String type) {
        return facilityService.getByType(type).stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();
    }

    @GetMapping("/search")
    public List<FacilityDTO> search(@RequestParam String keyword) {
        return facilityService.searchByName(keyword).stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();
    }

    @GetMapping("/with-schedules")
    public List<FacilityDTO> getFacilitiesWithSchedules() {
        return facilityService.getFacilitiesWithSchedules().stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<FacilityDTO> create(@Valid @RequestBody FacilityDTO dto) {
        Facility created = facilityService.create(modelMapper.map(dto, Facility.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, FacilityDTO.class));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<FacilityDTO>> createBatch(
            @Valid @RequestBody FacilityBatchDTO batchDTO) {

        List<Facility> facilities = batchDTO.getFacilities().stream()
                .map(dto -> modelMapper.map(dto, Facility.class))
                .toList();

        List<FacilityDTO> created = facilityService.createBatch(facilities).stream()
                .map(f -> modelMapper.map(f, FacilityDTO.class))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public FacilityDTO update(@PathVariable Long id, @Valid @RequestBody FacilityDTO dto) {
        Facility updated = facilityService.update(id, modelMapper.map(dto, Facility.class));
        return modelMapper.map(updated, FacilityDTO.class);
    }

    @PatchMapping("/{id}")
    public FacilityDTO patch(@PathVariable Long id, @RequestBody FacilityDTO dto) {
        Facility existing = facilityService.getById(id);
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getType() != null) existing.setType(dto.getType());
        return modelMapper.map(facilityService.update(id, existing), FacilityDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}