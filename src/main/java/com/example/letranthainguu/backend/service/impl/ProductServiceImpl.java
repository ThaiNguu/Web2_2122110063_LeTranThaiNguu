package com.example.letranthainguu.backend.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.letranthainguu.backend.domain.Cart;
import com.example.letranthainguu.backend.domain.Category;
import com.example.letranthainguu.backend.domain.Product;
import com.example.letranthainguu.backend.exceptions.APIException;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.dto.BrandDTO;
import com.example.letranthainguu.backend.dto.CartDTO;
import com.example.letranthainguu.backend.dto.CategoryDTO;
import com.example.letranthainguu.backend.dto.ProductDTO;
import com.example.letranthainguu.backend.dto.ProductResponse;
import com.example.letranthainguu.backend.repository.CartRepo;
import com.example.letranthainguu.backend.repository.CategoryRepo;
import com.example.letranthainguu.backend.repository.ProductRepo;
import com.example.letranthainguu.backend.service.CartService;
import com.example.letranthainguu.backend.service.FileService;
import com.example.letranthainguu.backend.service.ProductService;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

        @Autowired
        private ProductRepo productRepo;

        @Autowired
        private CategoryRepo categoryRepo;

        @Autowired
        private CartRepo cartRepo;

        @Autowired
        private CartService cartService;

        @Autowired
        private FileService fileService;

        @Autowired
        private ModelMapper modelMapper;

        @Value("${project.image}")
        private String path;

        @Override
        public ProductDTO addProduct(Long categoryId, Product product) {
                Category category = categoryRepo.findById(categoryId)
                                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

                boolean isProductNotPresent = category.getProducts().stream()
                                .noneMatch(p -> p.getProductName().equals(product.getProductName())
                                                && p.getDescription().equals(product.getDescription()));

                if (isProductNotPresent) {
                        product.setImage("default.png");
                        product.setCategory(category);

                        Double specialPrice = product.getPrice()
                                        - ((product.getDiscount() * 0.01) * product.getPrice());
                        product.setSpecialPrice(specialPrice);

                        Product savedProduct = productRepo.save(product);
                        return mapToDTO(savedProduct);
                } else {
                        throw new APIException("Product already exists!");
                }
        }

        @Override
        public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

                Page<Product> pageProducts = productRepo.findAll(pageDetails);

                List<Product> products = pageProducts.getContent();

                List<ProductDTO> productDTOs = products.stream()
                                .map(product -> mapToDTO(product))
                                .collect(Collectors.toList());

                ProductResponse productResponse = new ProductResponse();

                productResponse.setContent(productDTOs);
                productResponse.setPageNumber(pageProducts.getNumber());
                productResponse.setPageSize(pageProducts.getSize());
                productResponse.setTotalElements(pageProducts.getTotalElements());
                productResponse.setTotalPages(pageProducts.getTotalPages());
                productResponse.setLastPage(pageProducts.isLast());

                return productResponse;
        }

        @Override
        public ProductDTO getProductById(Long productId) {
                Optional<Product> productOptional = productRepo.findById(productId);

                if (productOptional.isPresent()) {
                        Product product = productOptional.get();
                        return mapToDTO(product);
                } else {
                        throw new ResourceNotFoundException("Product", "productId", productId);
                }
        }

        @Override
        public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder) {

                // Category category = categoryRepo.findById(categoryId)
                // .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                // categoryId));

                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

                Page<Product> pageProducts = productRepo.findByCategoryCategoryId(categoryId, pageDetails);

                List<Product> products = pageProducts.getContent();

                // if (products.isEmpty()) {
                // throw new APIException(category.getCategoryName() + " category doesn't
                // contain any products !!!");
                // }

                List<ProductDTO> productDTOs = products.stream()
                                .map(p -> mapToDTO(p))
                                .collect(Collectors.toList());

                ProductResponse productResponse = new ProductResponse();
                productResponse.setContent(productDTOs);
                productResponse.setPageNumber(pageProducts.getNumber());
                productResponse.setPageSize(pageProducts.getSize());
                productResponse.setTotalElements(pageProducts.getTotalElements());
                productResponse.setTotalPages(pageProducts.getTotalPages());
                productResponse.setLastPage(pageProducts.isLast());

                return productResponse;
        }

        @Override
        public ProductResponse searchProductByKeyword(String keyword, Long categoryId, Integer pageNumber,
                        Integer pageSize,
                        String sortBy,
                        String sortOrder) {
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

                Page<Product> pageProducts = productRepo.findByProductNameLike("%" + keyword + "%", pageDetails);
                List<Product> products = pageProducts.getContent();

                // if (products.size() == 0) {
                // throw new APIException("Products not found with keyword: " + keyword);
                // }
                if (categoryId != 0 && categoryId != null) {
                        products = products.stream()
                                        .filter(product -> {
                                                if (product.getCategory() != null
                                                                && product.getCategory().getCategoryId() != null) {
                                                        Long productCategoryId = product.getCategory().getCategoryId();
                                                        return productCategoryId == categoryId;
                                                }
                                                return false;
                                        })
                                        .collect(Collectors.toList());
                }

                List<ProductDTO> productDTOs = products.stream()
                                .map(product -> mapToDTO(product))
                                .collect(Collectors.toList());

                ProductResponse productResponse = new ProductResponse();

                productResponse.setContent(productDTOs);
                productResponse.setPageNumber((pageProducts.getNumber()));

                productResponse.setPageSize((pageProducts.getSize()));

                productResponse.setTotalElements((pageProducts.getTotalElements()));

                productResponse.setTotalPages((pageProducts.getTotalPages()));

                productResponse.setLastPage((pageProducts.isLast()));

                return productResponse;
        }

        @Override
        public ProductDTO updateProduct(Long productId, Product product) {
                Product productFromDB = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

                if (productFromDB == null) {
                        throw new APIException("Product not found with productId: " + productId);
                }

                product.setImage(productFromDB.getImage());
                product.setProductId(productId);
                product.setCategory(productFromDB.getCategory());

                double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
                product.setSpecialPrice(specialPrice);

                Product savedProduct = productRepo.save(product);

                List<Cart> carts = cartRepo.findCartsByProductId(productId);

                List<CartDTO> cartDTOs = carts.stream().map(cart -> {
                        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                        List<ProductDTO> products = cart.getCartItems().stream()
                                        .map(p -> mapToDTO(p.getProduct()))
                                        .collect(Collectors.toList());

                        cartDTO.setProducts(products);
                        return cartDTO;
                }).collect(Collectors.toList());

                cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

                return mapToDTO(savedProduct);
        }

        @Override
        public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
                Product productFromDB = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

                if (productFromDB == null) {
                        throw new APIException("Product not found with productId: " + productId);
                }

                String fileName = fileService.uploadImage(path, image);
                productFromDB.setImage(fileName);

                Product updatedProduct = productRepo.save(productFromDB);

                return mapToDTO(updatedProduct);
        }

        @Override
        public String deleteProduct(Long productId) {
                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

                List<Cart> carts = cartRepo.findCartsByProductId(productId);

                carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

                productRepo.delete(product);

                return "Product with productId: " + productId + " deleted successfully !!!";
        }

        @Override
        public InputStream getProductImage(String fileName) throws FileNotFoundException {
                return fileService.getResource(path, fileName);
        }

        private ProductDTO mapToDTO(Product product) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProductId(product.getProductId());
                productDTO.setProductName(product.getProductName());

                // Add category mapping
                if (product.getCategory() != null) {
                        CategoryDTO categoryDTO = new CategoryDTO();
                        categoryDTO.setCategoryId(product.getCategory().getCategoryId());
                        categoryDTO.setCategoryName(product.getCategory().getCategoryName());
                        productDTO.setCategory(categoryDTO);
                }

                // Add brand mapping
                if (product.getBrand() != null) {
                        BrandDTO brandDTO = new BrandDTO();
                        brandDTO.setBrandId(product.getBrand().getBrandId());
                        brandDTO.setBrandName(product.getBrand().getBrandName());
                        productDTO.setBrand(brandDTO);
                }

                productDTO.setImage(product.getImage());
                productDTO.setDescription(product.getDescription());
                productDTO.setQuantity(product.getQuantity());
                productDTO.setPrice(product.getPrice());
                productDTO.setDiscount(product.getDiscount());
                productDTO.setSpecialPrice(product.getSpecialPrice());
                return productDTO;
        }

        @Override
        public ProductResponse searchProductByKeyword(String keyword, int i, Integer pageSize, Object object,
                        String sortOrder) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'searchProductByKeyword'");
        }
}