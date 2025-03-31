package com.example.letranthainguu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.example.letranthainguu.backend.config.AppConstants;
import com.example.letranthainguu.backend.domain.Brand;
import com.example.letranthainguu.backend.dto.BrandDTO;
import com.example.letranthainguu.backend.repository.BrandRepo;
import com.example.letranthainguu.backend.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin(origins = "*")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/admin/brand")
    public ResponseEntity<BrandDTO> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        BrandDTO savedBrandDTO = brandService.createBrand(brandDTO);
        return new ResponseEntity<>(savedBrandDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/brands")
    public ResponseEntity<List<BrandDTO>> getBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @GetMapping("/public/brands/{brandId}")
    public ResponseEntity<BrandDTO> getOneBrand(@PathVariable Long brandId) {
        BrandDTO brandDTO = brandService.getBrandById(brandId);
        return new ResponseEntity<>(brandDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/brands/{brandId}")
    public ResponseEntity<BrandDTO> updateBrand(@RequestBody BrandDTO brandDTO, @PathVariable Long brandId) {
        BrandDTO updatedBrandDTO = brandService.updateBrand(brandId, brandDTO);
        return new ResponseEntity<>(updatedBrandDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/brands/{brandId}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
        return new ResponseEntity<>("Brand deleted successfully", HttpStatus.OK);
    }
}
