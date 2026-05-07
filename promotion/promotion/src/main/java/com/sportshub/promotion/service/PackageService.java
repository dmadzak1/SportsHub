package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<Package> getAll() {
        return packageRepository.findAll();
    }

    public Package getById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));
    }

    public List<Package> getByName(String name) {
        return packageRepository.findByName(name.toUpperCase());
    }

    public Package create(Package pkg) {
        return packageRepository.save(pkg);
    }

    public Package update(Long id, Package updated) {
        Package existing = getById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        return packageRepository.save(existing);
    }

    public void delete(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Package", id);
        }
        packageRepository.deleteById(id);
    }
}
