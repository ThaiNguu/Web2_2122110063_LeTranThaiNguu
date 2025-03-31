package com.example.letranthainguu.backend.service;

import java.util.List;

import com.example.letranthainguu.backend.domain.Brand;
import com.example.letranthainguu.backend.dto.BrandDTO;

import jakarta.validation.Valid;

public interface BrandService {
    BrandDTO createBrand(BrandDTO brandDTO); // Fixed parameter type

    List<BrandDTO> getAllBrands();

    BrandDTO getBrandById(Long brandId);

    BrandDTO updateBrand(Long brandId, BrandDTO brandDTO);

    void deleteBrand(Long brandId);
}