package com.example.letranthainguu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.letranthainguu.backend.domain.Category;

@Repository
public interface CategoryRepo extends JpaRepository < Category, Long > {

    Category findByCategoryName(String categoryName);
}
