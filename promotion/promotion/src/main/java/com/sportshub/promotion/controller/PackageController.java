package com.sportshub.promotion.controller;

import com.sportshub.promotion.dto.PackageDTO;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.service.PackageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final ModelMapper modelMapper;

    public PackageController (PackageService packageService, ModelMapper modelMapper) {
        this.packageService = packageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PackageDTO> getAll() {
        return packageService.getAll().stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @GetMapping("/paged")
    public Page<PackageDTO> getAllPaged(
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return packageService.getAllPaged(pageable)
                .map(p -> modelMapper.map(p, PackageDTO.class));
    }

    @GetMapping("/{id}")
    public PackageDTO getById(@PathVariable Long id) {
        return modelMapper.map(packageService.getById(id), PackageDTO.class);
    }

    @GetMapping("/{id}/with-promotions")
    public PackageDTO getByIdWithPromotions(@PathVariable Long id) {
        return modelMapper.map(packageService.getByIdWithPromotions(id), PackageDTO.class);
    }

    @GetMapping("/name/{name}")
    public List<PackageDTO> getByName(@PathVariable String name) {
        return packageService.getByName(name).stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @GetMapping("/price-range")
    public List<PackageDTO> getByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice
    ) {
        return packageService.getByPriceRange(minPrice, maxPrice).stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<PackageDTO> create(@Valid @RequestBody PackageDTO dto) {
        Package created = packageService.create(new Package(dto.getName(), dto.getPrice()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, PackageDTO.class));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PackageDTO>> createBatch(@Valid @RequestBody List<PackageDTO> dtos) {
        List<Package> packages = dtos.stream()
                .map(dto -> new Package(dto.getName(), dto.getPrice()))
                .toList();

        List<PackageDTO> created = packageService.createBatch(packages).stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public PackageDTO patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Package updated = packageService.patch(id, updates);
        return modelMapper.map(updated, PackageDTO.class);
    }

    @PatchMapping("/increase-prices")
    public List<PackageDTO> increasePrices(
            @RequestParam List<Long> packageIds,
            @RequestParam Double percentage
    ) {
        return packageService.increasePricesForPackages(packageIds, percentage).stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @PutMapping("/{id}")
    public PackageDTO update(@PathVariable Long id, @Valid @RequestBody PackageDTO dto) {
        Package updated = packageService.update(id, new Package(dto.getName(), dto.getPrice()));
        return modelMapper.map(updated, PackageDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}