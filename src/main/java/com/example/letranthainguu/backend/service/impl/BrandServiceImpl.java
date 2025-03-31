package com.example.letranthainguu.backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.letranthainguu.backend.domain.Brand;
import com.example.letranthainguu.backend.dto.BrandDTO;
import com.example.letranthainguu.backend.exceptions.APIException;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.repository.BrandRepo;
import com.example.letranthainguu.backend.service.BrandService;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepo brandRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        if (brandRepo.findByBrandName(brandDTO.getBrandName()) != null) {
            throw new APIException("Brand with the name '" + brandDTO.getBrandName() + "' already exists!");
        }
        Brand brand = modelMapper.map(brandDTO, Brand.class);
        Brand savedBrand = brandRepo.save(brand);
        return modelMapper.map(savedBrand, BrandDTO.class);
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        List<Brand> brands = brandRepo.findAll();
        return brands.stream().map(brand -> modelMapper.map(brand, BrandDTO.class)).collect(Collectors.toList());
    }

    @Override
    public BrandDTO getBrandById(Long brandId) {
        Brand brand = brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));
        return modelMapper.map(brand, BrandDTO.class);
    }

    @Override
    public BrandDTO updateBrand(Long brandId, BrandDTO brandDTO) {
        Brand existingBrand = brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));
        existingBrand.setBrandName(brandDTO.getBrandName());
        Brand updatedBrand = brandRepo.save(existingBrand);
        return modelMapper.map(updatedBrand, BrandDTO.class);
    }

    @Override
    public void deleteBrand(Long brandId) {
        Brand brand = brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));
        brandRepo.delete(brand);
    }
}
