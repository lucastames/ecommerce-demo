package org.tames.ecommercecrud.modules.product.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.repository.CategoryRepository;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.exception.ProductNotFoundException;
import org.tames.ecommercecrud.modules.product.mapper.ProductMapper;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;

@Service
public class ProductService {
  private static final Logger log = LogManager.getLogger(ProductService.class);
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  public ProductService(
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productMapper = productMapper;
  }

  public ProductResponseDto getProductById(Long id) {
    log.info("Get product by ID: {}", id);

    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    return productMapper.toDto(product);
  }

  @Transactional
  public ProductResponseDto createProduct(SaveProductRequestDto saveProductRequestDto) {
    log.info("Creating new product");

    Set<Category> categories =
        saveProductRequestDto.categoryIds().stream()
            .map(categoryRepository::getReferenceById)
            .collect(Collectors.toSet());

    Product product = productMapper.toEntity(saveProductRequestDto, categories);

    return productMapper.toDto(productRepository.save(product));
  }

  @Transactional
  public ProductResponseDto updateProduct(Long id, SaveProductRequestDto saveProductRequestDto) {
    log.info("Updating product with ID: {}", id);

    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    Set<Category> categories =
        new HashSet<>(categoryRepository.findAllById(saveProductRequestDto.categoryIds()));
    productMapper.updateEntityFromDto(product, saveProductRequestDto, categories);

    return productMapper.toDto(productRepository.save(product));
  }

  @Transactional
  public void deleteProductById(Long id) {
    log.info("Deleting product with ID: {}", id);

    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    productRepository.delete(product);
  }
}
