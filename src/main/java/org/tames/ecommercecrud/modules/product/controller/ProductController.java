package org.tames.ecommercecrud.modules.product.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.service.ProductService;

@RestController
@RequestMapping("products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  // @GetMapping
  // public ResponseEntity<Void> getProdutos() {
  //  return ResponseEntity.noContent().build();
  // }

  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
    return ResponseEntity.ok(productService.getProductById(productId));
  }

  @PostMapping
  public ResponseEntity<ProductResponseDto> createProduct(
      @RequestBody @Valid SaveProductRequestDto saveProductRequestDto, UriComponentsBuilder ucb) {
    ProductResponseDto productResponseDto = productService.createProduct(saveProductRequestDto);
    return ResponseEntity.created(ucb.path("/products/{id}").build(productResponseDto.id()))
        .body(productResponseDto);
  }

  @PutMapping("/{productId}")
  public ResponseEntity<ProductResponseDto> updateProduct(
      @PathVariable Long productId,
      @RequestBody @Valid SaveProductRequestDto saveProductRequestDto) {
    return ResponseEntity.ok(productService.updateProduct(productId, saveProductRequestDto));
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
    productService.deleteProductById(productId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{productId}/reviews")
  public ResponseEntity<Void> getProductReviews() {
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{productId}/reviews")
  public ResponseEntity<Void> createProductReview() {
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{productId}/reviews/{reviewId}")
  public ResponseEntity<Void> updateProductReview() {
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{productId}/reviews/{reviewId}")
  public ResponseEntity<Void> deleteProductReview() {
    return ResponseEntity.noContent().build();
  }
}
