package com.sportshub.facility.service;

import com.sportshub.facility.model.Facility;
import com.sportshub.facility.repository.FacilityRepository;
import com.sportshub.facility.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    public Facility getById(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
    }

    public List<Facility> getByType(String type) {
        return facilityRepository.findByType(type.toUpperCase());
    }

    public Facility create(Facility facility) {
        return facilityRepository.save(facility);
    }

    public Facility update(Long id, Facility updated) {
        Facility existing = getById(id);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        return facilityRepository.save(existing);
    }

    public void delete(Long id) {
        if (!facilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facility", id);
        }
        facilityRepository.deleteById(id);
    }
}