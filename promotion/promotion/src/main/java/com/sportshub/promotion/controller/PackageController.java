package com.sportshub.promotion.controller;

import com.sportshub.promotion.dto.PackageDTO;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.service.PackageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final ModelMapper modelMapper;

    public PackageController(PackageService packageService, ModelMapper modelMapper) {
        this.packageService = packageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PackageDTO> getAll() {
        return packageService.getAll().stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    public PackageDTO getById(@PathVariable Long id) {
        return modelMapper.map(packageService.getById(id), PackageDTO.class);
    }

    @GetMapping("/name/{name}")
    public List<PackageDTO> getByName(@PathVariable String name) {
        return packageService.getByName(name).stream()
                .map(p -> modelMapper.map(p, PackageDTO.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<PackageDTO> create(@Valid @RequestBody PackageDTO dto) {
        Package created = packageService.create(new Package(dto.getName(), dto.getPrice()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, PackageDTO.class));
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
