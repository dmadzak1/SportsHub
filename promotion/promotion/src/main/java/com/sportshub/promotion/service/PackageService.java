package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.repository.PackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<Package> getAll() {
        return packageRepository.findAll();
    }

    public Page<Package> getAllPaged(Pageable pageable) {
        return packageRepository.findAll(pageable);
    }

    public Package getById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));
    }

    public Package getByIdWithPromotions(Long id) {
        return packageRepository.findByIdWithPromotions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", id));
    }

    public List<Package> getByName(String name) {
        return packageRepository.findByName(name.toUpperCase());
    }

    public List<Package> getByPriceRange(Double minPrice, Double maxPrice) {
        return packageRepository.findPackagesByPriceRange(minPrice, maxPrice);
    }

    public Package create(Package pkg) {
        return packageRepository.save(pkg);
    }

    public List<Package> createBatch(List<Package> packages) {
        return packageRepository.saveAll(packages);
    }

    public Package update(Long id, Package updated) {
        Package existing = getById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        return packageRepository.save(existing);
    }

    public Package patch(Long id, Map<String, Object> updates) {
        Package existing = getById(id);

        if (updates.containsKey("name")) {
            existing.setName(updates.get("name").toString());
        }

        if (updates.containsKey("price")) {
            existing.setPrice(Double.valueOf(updates.get("price").toString()));
        }

        return packageRepository.save(existing);
    }

    @Transactional
    public List<Package> increasePricesForPackages(List<Long> packageIds, Double percentage) {
        List<Package> packages = packageRepository.findAllById(packageIds);

        if (packages.size() != packageIds.size()) {
            throw new IllegalArgumentException("Some packages were not found.");
        }

        for (Package pkg : packages) {
            Double oldPrice = pkg.getPrice();
            Double newPrice = oldPrice + oldPrice * percentage / 100;
            pkg.setPrice(newPrice);
        }

        return packageRepository.saveAll(packages);
    }

    public void delete(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Package", id);
        }
        packageRepository.deleteById(id);
    }
}