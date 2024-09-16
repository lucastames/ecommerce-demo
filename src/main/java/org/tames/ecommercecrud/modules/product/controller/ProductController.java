package org.tames.ecommercecrud.modules.product.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.service.ProductService;
import org.tames.ecommercecrud.modules.product.service.ReviewService;
import org.tames.ecommercecrud.modules.product.specification.ProductSpecs.ProductFilter;
import org.tames.ecommercecrud.modules.product.specification.ReviewSpecs.ReviewFilter;
import org.tames.ecommercecrud.modules.user.entity.User;

@RestController
@RequestMapping("products")
public class ProductController {
  private final ProductService productService;
  private final ReviewService reviewService;

  public ProductController(ProductService productService, ReviewService reviewService) {
    this.productService = productService;
    this.reviewService = reviewService;
  }

  @GetMapping
  public ResponseEntity<PagedModel<ProductResponseDto>> getProducts(
      Pageable pageable, ProductFilter productFilter) {
    return ResponseEntity.ok(productService.getProducts(pageable, productFilter));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductResponseDto> createProduct(
      @RequestBody @Valid SaveProductRequestDto saveProductRequestDto, UriComponentsBuilder ucb) {
    ProductResponseDto productResponseDto = productService.createProduct(saveProductRequestDto);

    return ResponseEntity.created(ucb.path("/products/{productId}").build(productResponseDto.id()))
        .body(productResponseDto);
  }

  @GetMapping("{productId}")
  public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
    return ResponseEntity.ok(productService.getProductById(productId));
  }

  @PutMapping("{productId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductResponseDto> updateProduct(
      @PathVariable Long productId,
      @RequestBody @Valid SaveProductRequestDto saveProductRequestDto) {
    return ResponseEntity.ok(productService.updateProduct(productId, saveProductRequestDto));
  }

  @DeleteMapping("{productId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
    productService.deleteProductById(productId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("{productId}/reviews")
  public ResponseEntity<PagedModel<ReviewResponseDto>> getProductReviews(
      @PathVariable Long productId, Pageable pageable, ReviewFilter reviewFilter) {
    return ResponseEntity.ok(reviewService.getReviews(productId, pageable, reviewFilter));
  }

  @PostMapping("{productId}/reviews")
  public ResponseEntity<ReviewResponseDto> createProductReview(
      @PathVariable Long productId,
      @Valid @RequestBody SaveReviewRequestDto saveReviewRequestDto,
      @AuthenticationPrincipal User user,
      UriComponentsBuilder ucb) {
    ReviewResponseDto createdReview =
        reviewService.createReview(productId, user, saveReviewRequestDto);

    return ResponseEntity.created(
            ucb.path("/products/{productId}/reviews/{reviewId}")
                .build(productId, createdReview.id()))
        .body(createdReview);
  }

  @PutMapping("{productId}/reviews/{reviewId}")
  public ResponseEntity<ReviewResponseDto> updateProductReview(
      @Valid @RequestBody SaveReviewRequestDto saveReviewRequestDto,
      @PathVariable Long productId,
      @PathVariable Long reviewId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(
        reviewService.updateReview(saveReviewRequestDto, productId, reviewId, user));
  }

  @DeleteMapping("{productId}/reviews/{reviewId}")
  public ResponseEntity<Void> deleteProductReview(
      @PathVariable Long productId,
      @PathVariable Long reviewId,
      @AuthenticationPrincipal User user) {
    reviewService.deleteReview(productId, reviewId, user);

    return ResponseEntity.noContent().build();
  }
}
