package org.tames.ecommercecrud.modules.product.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;
import org.springframework.test.web.servlet.MockMvc;
import org.tames.ecommercecrud.annotations.WithMockAdmin;
import org.tames.ecommercecrud.annotations.WithMockCustomer;
import org.tames.ecommercecrud.config.ControllerTestConfig;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.enums.Rating;
import org.tames.ecommercecrud.modules.product.exception.ProductNotFoundException;
import org.tames.ecommercecrud.modules.product.exception.ReviewDoesNotBelongToProductException;
import org.tames.ecommercecrud.modules.product.exception.ReviewNotFoundException;
import org.tames.ecommercecrud.modules.product.exception.ReviewOwnershipException;
import org.tames.ecommercecrud.modules.product.service.ProductService;
import org.tames.ecommercecrud.modules.product.service.ReviewService;
import org.tames.ecommercecrud.modules.product.specification.ProductSpecs.ProductFilter;
import org.tames.ecommercecrud.modules.product.specification.ReviewSpecs.ReviewFilter;
import org.tames.ecommercecrud.modules.user.entity.User;

@WebMvcTest(ProductController.class)
@Import(ControllerTestConfig.class)
@WithMockCustomer
public class ProductControllerTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean ProductService productService;
  @MockBean ReviewService reviewService;

  private ProductResponseDto productResponseDto;
  private SaveProductRequestDto productRequestDto;
  private SaveProductRequestDto invalidProductRequestDto;
  private ReviewResponseDto reviewResponseDto;
  private SaveReviewRequestDto reviewRequestDto;
  private SaveReviewRequestDto invalidReviewRequestDto;

  @BeforeEach
  void setUp() {
    productResponseDto =
        new ProductResponseDto(
            1L,
            "Product 1",
            BigDecimal.valueOf(12.5),
            "Product 1 description",
            10,
            List.of(
                new CategoryResponseDto(1L, "Category 1"),
                new CategoryResponseDto(2L, "Category 2")));

    productRequestDto =
        new SaveProductRequestDto(
            "Product 1", BigDecimal.valueOf(12.5), "Product 1 description", 10, List.of(1L, 2L));

    invalidProductRequestDto =
        new SaveProductRequestDto("", BigDecimal.valueOf(-1.25), "", 20, List.of(1L));

    reviewResponseDto =
        new ReviewResponseDto(
            1L, "Review description 1", LocalDate.parse("2024-10-10"), Rating.GOOD, "customer");

    reviewRequestDto = new SaveReviewRequestDto("Review description 1", Rating.GOOD);

    invalidReviewRequestDto = new SaveReviewRequestDto("", Rating.EXCELLENT);
  }

  @Test
  void
      testGetProducts_WhenProvidingAllParameters_ShouldReturnPagedModelWithProductResponseDtoAndOkStatus()
          throws Exception {
    List<ProductResponseDto> products =
        List.of(
            new ProductResponseDto(
                1L,
                "Product 1",
                BigDecimal.valueOf(2.99),
                "Product 1 description",
                10,
                Collections.emptyList()),
            new ProductResponseDto(
                2L,
                "Product 2",
                BigDecimal.valueOf(9.99),
                "Product 2 description",
                10,
                Collections.emptyList()));
    Pageable pageable = PageRequest.of(0, 25, Sort.by("name"));
    ProductFilter productFilter = new ProductFilter(null, null, null, 10);
    given(productService.getProducts(pageable, productFilter))
        .willReturn(new PagedModel<>(new PageImpl<>(products, pageable, products.size())));

    mockMvc
        .perform(
            get("/products")
                .param("page", "0")
                .param("size", "25")
                .param("sort", "name")
                .param("stockQuantity", "10")
                .accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.size()").value(2),
            jsonPath("$..name").value(contains("Product 1", "Product 2")));
  }

  @Test
  void testGetProduct_WhenExistingProductIdIsProvided_ShouldReturnProductResponseDtoOkStatus()
      throws Exception {
    given(productService.getProductById(1L)).willReturn(productResponseDto);

    mockMvc
        .perform(get("/products/{productId}", 1L).accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(productResponseDto.id()),
            jsonPath("$.name").value(productResponseDto.name()),
            jsonPath("$.price").value(productResponseDto.price()),
            jsonPath("$.description").value(productResponseDto.description()),
            jsonPath("$.stockQuantity").value(productResponseDto.stockQuantity()),
            jsonPath("$.categories.length()").value(productResponseDto.categories().size()),
            jsonPath("$.categories..name")
                .value(
                    containsInAnyOrder(
                        productResponseDto.categories().stream()
                            .map(CategoryResponseDto::name)
                            .toArray())));
  }

  @Test
  void testGetProduct_WhenNonExistingProductIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(productService.getProductById(anyLong())).willThrow(new ProductNotFoundException(99L));

    mockMvc
        .perform(get("/products/{productId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockAdmin
  void testCreateProduct_WhenValidDtoIsProvided_ShouldReturnProductResponseDtoAndCreatedStatus()
      throws Exception {
    given(productService.createProduct(productRequestDto)).willReturn(productResponseDto);

    mockMvc
        .perform(
            post("/products")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
        .andExpectAll(
            status().isCreated(),
            header().exists("Location"),
            jsonPath("$.id").value(productResponseDto.id()),
            jsonPath("$.name").value(productResponseDto.name()),
            jsonPath("$.price").value(productResponseDto.price()),
            jsonPath("$.description").value(productResponseDto.description()),
            jsonPath("$.stockQuantity").value(productResponseDto.stockQuantity()),
            jsonPath("$.categories.length()").value(productResponseDto.categories().size()),
            jsonPath("$.categories..name")
                .value(
                    containsInAnyOrder(
                        productResponseDto.categories().stream()
                            .map(CategoryResponseDto::name)
                            .toArray())));
  }

  @Test
  @WithMockAdmin
  void testCreateProduct_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            post("/products")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateProduct_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            post("/products")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockAdmin
  void testUpdateProduct_WhenValidDtoIsProvided_ShouldReturnProductResponseDtoAndOkStatus()
      throws Exception {
    given(productService.updateProduct(1L, productRequestDto)).willReturn(productResponseDto);

    mockMvc
        .perform(
            put("/products/{productId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(productResponseDto.id()),
            jsonPath("$.name").value(productResponseDto.name()),
            jsonPath("$.price").value(productResponseDto.price()),
            jsonPath("$.description").value(productResponseDto.description()),
            jsonPath("$.stockQuantity").value(productResponseDto.stockQuantity()),
            jsonPath("$.categories.length()").value(productResponseDto.categories().size()),
            jsonPath("$.categories..name")
                .value(
                    containsInAnyOrder(
                        productResponseDto.categories().stream()
                            .map(CategoryResponseDto::name)
                            .toArray())));
  }

  @Test
  @WithMockAdmin
  void testUpdateProduct_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            put("/products/{productId}", 1L)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockAdmin
  void testUpdateProduct_WhenNonExistingProductIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(productService.updateProduct(anyLong(), any(SaveProductRequestDto.class)))
        .willThrow(new ProductNotFoundException(99L));

    mockMvc
        .perform(
            put("/products/{productId}", 99L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateProduct_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            put("/products/{productId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockAdmin
  void testDeleteProduct_WhenExistingProductIdIsProvided_ShouldReturnNoContentStatus()
      throws Exception {
    mockMvc
        .perform(delete("/products/{productId}", 1L).accept(APPLICATION_JSON))
        .andExpectAll(status().isNoContent());

    then(productService).should().deleteProductById(1L);
  }

  @Test
  @WithMockAdmin
  void testDeleteProduct_WhenNonExistingProductIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    willThrow(new ProductNotFoundException(99L)).given(productService).deleteProductById(anyLong());

    mockMvc
        .perform(delete("/products/{productId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteProduct_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(delete("/products/{productId}", 1L).accept(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void
      testGetReviews_WhenProvidingAllParameters_ShouldReturnPagedModelWithReviewResponseDtoAndOkStatus()
          throws Exception {
    List<ReviewResponseDto> reviews =
        List.of(
            new ReviewResponseDto(
                1L, "Review 1 description", LocalDate.parse("2024-10-10"), Rating.GOOD, "customer"),
            new ReviewResponseDto(
                2L, "Review 2 description", LocalDate.parse("2024-10-10"), Rating.GOOD, "admin"));
    Pageable pageable = PageRequest.of(0, 25, Sort.by("description"));
    ReviewFilter reviewFilter = new ReviewFilter(null, null, Rating.GOOD);
    given(reviewService.getReviews(1L, pageable, reviewFilter))
        .willReturn(new PagedModel<>(new PageImpl<>(reviews, pageable, reviews.size())));

    mockMvc
        .perform(
            get("/products/{productId}/reviews", 1L)
                .param("page", "0")
                .param("size", "25")
                .param("sort", "description")
                .param("rating", "GOOD")
                .accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.size()").value(2),
            jsonPath("$..description")
                .value(contains("Review 1 description", "Review 2 description")));
  }

  @Test
  void testCreateReview_WhenValidDtoIsProvided_ShouldReturnReviewResponseDtoAndCreatedStatus()
      throws Exception {
    given(reviewService.createReview(eq(1L), any(User.class), eq(reviewRequestDto)))
        .willReturn(reviewResponseDto);

    mockMvc
        .perform(
            post("/products/{productId}/reviews", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpectAll(
            status().isCreated(),
            header().exists("Location"),
            jsonPath("$.id").value(reviewResponseDto.id()),
            jsonPath("$.description").value(reviewResponseDto.description()),
            jsonPath("$.rating").value(reviewResponseDto.rating().toString()));
  }

  @Test
  void testCreateReview_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            post("/products/{productId}/reviews", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReviewRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateReview_WhenValidDtoIsProvided_ShouldReturnReviewResponseDtoAndOkStatus()
      throws Exception {
    given(reviewService.updateReview(eq(reviewRequestDto), eq(1L), eq(1L), any(User.class)))
        .willReturn(reviewResponseDto);

    mockMvc
        .perform(
            put("/products/{productId}/reviews/{reviewId}", 1L, 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(reviewResponseDto.id()),
            jsonPath("$.description").value(reviewResponseDto.description()),
            jsonPath("$.rating").value(reviewResponseDto.rating().toString()));
  }

  @Test
  void testUpdateReview_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            put("/products/{productId}/reviews/{reviewId}", 1L, 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReviewRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateReview_WhenReviewDoesntBelongToProductIdProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    given(reviewService.updateReview(eq(reviewRequestDto), eq(99L), eq(1L), any(User.class)))
        .willThrow(new ReviewDoesNotBelongToProductException(1L, 99L));

    mockMvc
        .perform(
            put("/products/{productId}/reviews/{reviewId}", 99L, 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateReview_WhenNonExistingReviewIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(reviewService.updateReview(eq(reviewRequestDto), eq(1L), eq(99L), any(User.class)))
        .willThrow(new ReviewNotFoundException(99L));

    mockMvc
        .perform(
            put("/products/{productId}/reviews/{reviewId}", 1L, 99L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateReview_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    given(
            reviewService.updateReview(
                any(SaveReviewRequestDto.class), anyLong(), anyLong(), any(User.class)))
        .willThrow(new ReviewOwnershipException(""));

    mockMvc
        .perform(
            put("/products/{productId}/reviews/{reviewId}", 1L, 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteReview_WhenExistingReviewIdIsProvided_ShouldReturnNoContentStatus()
      throws Exception {
    mockMvc
        .perform(
            delete("/products/{productId}/reviews/{reviewId}", 1L, 1L).accept(APPLICATION_JSON))
        .andExpectAll(status().isNoContent());
  }

  @Test
  void testDeleteReview_WhenNonExistingReviewIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    willThrow(new ReviewNotFoundException(99L))
        .given(reviewService)
        .deleteReview(eq(1L), eq(99L), any(User.class));

    mockMvc
        .perform(
            delete("/products/{productId}/reviews/{reviewId}", 1L, 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteReview_WhenReviewDoesntBelongToProductIdProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    willThrow(new ReviewDoesNotBelongToProductException(1L, 99L))
        .given(reviewService)
        .deleteReview(eq(99L), eq(1L), any(User.class));

    mockMvc
        .perform(
            delete("/products/{productId}/reviews/{reviewId}", 99L, 1L).accept(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testDeleteReview_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    willThrow(new ReviewOwnershipException(""))
        .given(reviewService)
        .deleteReview(anyLong(), anyLong(), any(User.class));

    mockMvc
        .perform(
            delete("/products/{productId}/reviews/{reviewId}", 1L, 1L).accept(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
