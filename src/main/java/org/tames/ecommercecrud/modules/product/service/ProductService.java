package org.tames.ecommercecrud.modules.product.service;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
import org.tames.ecommercecrud.modules.product.specification.ProductSpecs.ProductFilter;

@Service
@Transactional(readOnly = true)
public class ProductService {
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

  public PagedModel<ProductResponseDto> getProducts(
      Pageable pageable, ProductFilter specification) {
    Page<ProductResponseDto> productPage =
        productRepository.findAll(specification, pageable).map(productMapper::toDto);

    return new PagedModel<>(productPage);
  }

  public ProductResponseDto getProductById(Long id) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    return productMapper.toDto(product);
  }

  @Transactional
  public ProductResponseDto createProduct(SaveProductRequestDto saveProductRequestDto) {
    Set<Category> categories =
        new HashSet<>(categoryRepository.findAllById(saveProductRequestDto.categoryIds()));
    Product product = productMapper.toEntity(saveProductRequestDto, categories);

    return productMapper.toDto(productRepository.save(product));
  }

  @Transactional
  public ProductResponseDto updateProduct(Long id, SaveProductRequestDto saveProductRequestDto) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    Set<Category> categories =
        new HashSet<>(categoryRepository.findAllById(saveProductRequestDto.categoryIds()));

    productMapper.updateFromDto(product, saveProductRequestDto, categories);

    return productMapper.toDto(productRepository.save(product));
  }

  @Transactional
  public void deleteProductById(Long id) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    productRepository.delete(product);
  }
}
