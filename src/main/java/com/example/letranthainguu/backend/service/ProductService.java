package com.example.letranthainguu.backend.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.example.letranthainguu.backend.domain.Product;
import com.example.letranthainguu.backend.dto.ProductDTO;
import com.example.letranthainguu.backend.dto.ProductResponse;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, Product product);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, Product product);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    public InputStream getProductImage(String fileName) throws FileNotFoundException;

    ProductResponse searchProductByKeyword(String keyword, Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    String deleteProduct(Long productId);

    ProductDTO getProductById(Long productId);

    ProductResponse searchProductByKeyword(String keyword, int i, Integer pageSize, Object object, String sortOrder);
}