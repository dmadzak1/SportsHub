package com.sportshub.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.promotion.config.AppConfig;
import com.sportshub.promotion.dto.PackageDTO;
import com.sportshub.promotion.exception.GlobalExceptionHandler;
import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.service.PackageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PackageController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class PackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PackageService packageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsListOfPackages() throws Exception {
        Package pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        when(packageService.getAll()).thenReturn(List.of(pkg));

        mockMvc.perform(get("/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("INDIVIDUAL"))
                .andExpect(jsonPath("$[0].price").value(99.99));
    }

    @Test
    void getById_existingId_returnsPackage() throws Exception {
        Package pkg = new Package("GROUP", 149.99);
        pkg.setPackageId(1L);
        when(packageService.getById(1L)).thenReturn(pkg);

        mockMvc.perform(get("/packages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("GROUP"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(packageService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Package", 99L));

        mockMvc.perform(get("/packages/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        PackageDTO dto = new PackageDTO();
        dto.setName("FAMILY");
        dto.setPrice(199.99);

        Package saved = new Package("FAMILY", 199.99);
        saved.setPackageId(1L);
        when(packageService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("FAMILY"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        PackageDTO dto = new PackageDTO();
        // name is blank, price is null

        mockMvc.perform(post("/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/packages/1"))
                .andExpect(status().isNoContent());
    }
}
