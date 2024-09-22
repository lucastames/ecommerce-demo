package org.tames.ecommercecrud.modules.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.repository.CategoryRepository;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.exception.ProductNotFoundException;
import org.tames.ecommercecrud.modules.product.mapper.ProductMapper;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.product.specification.ProductSpecs.ProductFilter;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
  @Mock ProductRepository productRepository;
  @Mock ProductMapper productMapper;
  @Mock CategoryRepository categoryRepository;

  SaveProductRequestDto productRequestDto;
  Product mappedProduct;
  Product persistedProduct;
  ProductResponseDto productResponseDto;

  Category p1Category;
  CategoryResponseDto p1CategoryResponseDto;

  @BeforeEach
  void setUp() {
    p1Category = new Category("Category 1");
    p1Category.setId(1L);

    productRequestDto =
        new SaveProductRequestDto(
            "Product 1", BigDecimal.valueOf(15.00), "Product desc 1", 10, List.of(1L));

    mappedProduct = new Product("Product 1", BigDecimal.valueOf(15.00), "Product desc 1", 10);
    mappedProduct.addCategory(p1Category);

    persistedProduct = new Product("Product 1", BigDecimal.valueOf(15.00), "Product desc 1", 10);
    persistedProduct.setId(1L);
    persistedProduct.addCategory(p1Category);

    p1CategoryResponseDto = new CategoryResponseDto(1L, "Category 1");
    productResponseDto =
        new ProductResponseDto(
            1L,
            "Product 1",
            BigDecimal.valueOf(15.00),
            "Product desc 1",
            10,
            List.of(p1CategoryResponseDto));
  }

  @InjectMocks ProductService productService;

  @Test
  void
      testGetProducts_WhenPageParamsAndFilterAreProvided_ShouldReturnPagedModelWithProductResponseDto() {
    Pageable pageable = PageRequest.of(0, 50);
    ProductFilter productFilter = new ProductFilter("name", BigDecimal.valueOf(15.00), "desc", 10);

    given(productRepository.findAll(productFilter, pageable))
        .willReturn(new PageImpl<>(List.of(persistedProduct)));
    given(productMapper.toDto(persistedProduct)).willReturn(productResponseDto);

    PagedModel<ProductResponseDto> result = productService.getProducts(pageable, productFilter);

    assertThat(result).isNotNull();
    assertThat(result.getContent())
        .isNotNull()
        .first()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::name,
            ProductResponseDto::description,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            productResponseDto.id(),
            productResponseDto.name(),
            productResponseDto.description(),
            productResponseDto.price(),
            productResponseDto.stockQuantity());
  }

  @Test
  void testGetProduct_WhenExistingProductIdIsProvided_ShouldReturnProductResponseDto() {
    given(productRepository.findById(1L)).willReturn(Optional.of(persistedProduct));
    given(productMapper.toDto(persistedProduct)).willReturn(productResponseDto);

    ProductResponseDto result = productService.getProductById(1L);

    assertThat(result)
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::name,
            ProductResponseDto::description,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            productResponseDto.id(),
            productResponseDto.name(),
            productResponseDto.description(),
            productResponseDto.price(),
            productResponseDto.stockQuantity());
    assertThat(result.categories())
        .first()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(p1CategoryResponseDto.id(), p1CategoryResponseDto.name());
  }

  @Test
  void testGetProduct_WhenNonExistingProductIdIsProvided_ShouldThrowProductNotFoundException() {
    given(productRepository.findById(99L)).willThrow(new ProductNotFoundException(99L));

    assertThatThrownBy(() -> productService.getProductById(99L))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void testCreateProduct_WhenProductRequestDtoIsProvided_ShouldReturnCreatedProductResponseDto() {
    given(productMapper.toEntity(eq(productRequestDto), anySet())).willReturn(mappedProduct);
    given(productRepository.save(mappedProduct)).willReturn(persistedProduct);
    given(productMapper.toDto(persistedProduct)).willReturn(productResponseDto);

    ProductResponseDto result = productService.createProduct(productRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::name,
            ProductResponseDto::description,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            productResponseDto.id(),
            productResponseDto.name(),
            productResponseDto.description(),
            productResponseDto.price(),
            productResponseDto.stockQuantity());
    assertThat(result.categories())
        .first()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(p1CategoryResponseDto.id(), p1CategoryResponseDto.name());
  }

  @Test
  void
      testUpdateProduct_WhenProductRequestDtoAndProductIdAreProvided_ShouldReturnUpdatedProductResponseDto() {
    SaveProductRequestDto updatedProductRequestDto =
        new SaveProductRequestDto(
            "Product updated", BigDecimal.valueOf(10.00), "Product desc updated", 1, List.of(1L));
    ProductResponseDto updatedProductResponseDto =
        new ProductResponseDto(
            1L,
            "Product updated",
            BigDecimal.valueOf(10.00),
            "Product desc updated",
            1,
            List.of(p1CategoryResponseDto));
    Product updatedProduct =
        new Product("Product updated", BigDecimal.valueOf(10.00), "Product desc updated", 1);
    updatedProduct.setId(1L);
    updatedProduct.addCategory(p1Category);

    given(productRepository.findById(1L)).willReturn(Optional.of(persistedProduct));
    given(productRepository.save(persistedProduct)).willReturn(updatedProduct);
    given(productMapper.toDto(updatedProduct)).willReturn(updatedProductResponseDto);

    ProductResponseDto result = productService.updateProduct(1L, updatedProductRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::name,
            ProductResponseDto::description,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            updatedProductResponseDto.id(),
            updatedProductResponseDto.name(),
            updatedProductResponseDto.description(),
            updatedProductResponseDto.price(),
            updatedProductResponseDto.stockQuantity());
    assertThat(result.categories())
        .first()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(p1CategoryResponseDto.id(), p1CategoryResponseDto.name());
  }

  @Test
  void testUpdateProduct_WhenNonExistingProductIdIsProvided_ShouldThrowProductNotFoundException() {
    given(productRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> productService.updateProduct(99L, productRequestDto))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void testDeleteProduct_WhenExistingProductIdIsProvided_ShouldDeleteProduct() {
    given(productRepository.findById(1L)).willReturn(Optional.of(persistedProduct));

    productService.deleteProductById(1L);

    then(productRepository).should().delete(persistedProduct);
  }

  @Test
  void testDeleteProduct_WhenNonExistingProductIdIsProvided_ShouldThrowProductNotFoundException() {
    given(productRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> productService.deleteProductById(99L))
        .isInstanceOf(ProductNotFoundException.class);
  }
}
