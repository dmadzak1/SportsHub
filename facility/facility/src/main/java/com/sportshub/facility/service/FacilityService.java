package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.repository.FacilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Paginacija i sortiranje
    public Page<Facility> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return facilityRepository.findAll(pageable);
    }

    // Pretraga po nazivu
    public List<Facility> searchByName(String keyword) {
        return facilityRepository.searchByName(keyword);
    }

    // Tereni sa rasporedima
    public List<Facility> getFacilitiesWithSchedules() {
        return facilityRepository.findFacilitiesWithSchedules();
    }

    // Batch unos
    @Transactional
    public List<Facility> createBatch(List<Facility> facilities) {
        return facilityRepository.saveAll(facilities);
    }
}