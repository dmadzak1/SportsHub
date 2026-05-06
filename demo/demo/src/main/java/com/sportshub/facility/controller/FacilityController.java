package com.sportshub.facility.controller;

import com.sportshub.facility.dto.FacilityDTO;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.service.FacilityService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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

    @PostMapping
    public ResponseEntity<FacilityDTO> create(@Valid @RequestBody FacilityDTO dto) {
        Facility created = facilityService.create(modelMapper.map(dto, Facility.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, FacilityDTO.class));
    }

    @PutMapping("/{id}")
    public FacilityDTO update(@PathVariable Long id, @Valid @RequestBody FacilityDTO dto) {
        Facility updated = facilityService.update(id, modelMapper.map(dto, Facility.class));
        return modelMapper.map(updated, FacilityDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}