package com.example.letranthainguu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.letranthainguu.backend.domain.Brand;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Long> {
    
    Brand findByBrandName(String brandName);
}