package com.sportshub.facility.controller;

import com.sportshub.facility.model.Facility;
import com.sportshub.facility.repository.FacilityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    // GET /facilities
    @GetMapping
    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    // GET /facilities/1
    @GetMapping("/{id}")
    public ResponseEntity<Facility> getById(@PathVariable Long id) {
        return facilityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /facilities/type/TENNIS
    @GetMapping("/type/{type}")
    public List<Facility> getByType(@PathVariable String type) {
        return facilityRepository.findByType(type.toUpperCase());
    }

    // POST /facilities
    @PostMapping
    public Facility create(@RequestBody Facility facility) {
        return facilityRepository.save(facility);
    }

    // DELETE /facilities/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!facilityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        facilityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}