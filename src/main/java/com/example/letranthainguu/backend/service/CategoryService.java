package com.example.letranthainguu.backend.service;

import com.example.letranthainguu.backend.domain.Category;
import com.example.letranthainguu.backend.dto.CategoryDTO;
import com.example.letranthainguu.backend.dto.CategoryResponse;

public interface CategoryService {
    
    CategoryDTO createCategory(Category category);
    CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO updateCategory(Category category, Long categoryId);
    String deleteCategory(Long categoryId);
    CategoryDTO getCategoryById(Long categoryId);
}
