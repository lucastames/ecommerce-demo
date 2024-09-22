package org.tames.ecommercecrud.modules.product.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.enums.Rating;
import org.tames.ecommercecrud.util.CustomPagedModel;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/it-setup/product-setup.sql")
@Testcontainers
public class ProductIT {
  @Autowired private TestRestTemplate testRestTemplate;

  @ServiceConnection @Container
  PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

  private SaveProductRequestDto productRequestDto;
  private SaveReviewRequestDto reviewRequestDto;

  @BeforeEach
  void setUp() {
    productRequestDto =
        new SaveProductRequestDto(
            "Test product", BigDecimal.valueOf(12.0), "Test description", 25, List.of(1L, 2L, 3L));

    reviewRequestDto = new SaveReviewRequestDto("Product 1 is okay", Rating.OKAY);
  }

  @Test
  void testGetProducts() {
    ResponseEntity<CustomPagedModel<ProductResponseDto>> result =
        testRestTemplate.exchange(
            "/products?page=0&size=25&sort=name,asc&stockQuantity=10",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent())
        .isNotNull()
        .hasSize(2)
        .extracting(ProductResponseDto::name)
        .containsExactly("Product 1", "Product 3");
  }

  @Test
  void testCreateProduct() {
    ResponseEntity<ProductResponseDto> result =
        testRestTemplate.postForEntity("/products", productRequestDto, ProductResponseDto.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getHeaders().getLocation()).isNotNull().hasPath("/products/4");
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::description,
            ProductResponseDto::name,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            4L,
            productRequestDto.description(),
            productRequestDto.name(),
            productRequestDto.price(),
            productRequestDto.stockQuantity());
    assertThat(result.getBody().categories())
        .extracting(CategoryResponseDto::id)
        .containsExactlyInAnyOrderElementsOf(productRequestDto.categoryIds());
  }

  @Test
  void testGetProduct() {
    ResponseEntity<ProductResponseDto> result =
        testRestTemplate.getForEntity("/products/{productId}", ProductResponseDto.class, 1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::description,
            ProductResponseDto::name,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(1L, "Desc 1", "Product 1", new BigDecimal("12.50"), 10);

    assertThat(result.getBody().categories())
        .extracting(CategoryResponseDto::id)
        .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L, 3L));
  }

  @Test
  void testUpdateProduct() {
    ResponseEntity<ProductResponseDto> result =
        testRestTemplate.exchange(
            "/products/{productId}",
            HttpMethod.PUT,
            new HttpEntity<>(productRequestDto),
            ProductResponseDto.class,
            1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            ProductResponseDto::id,
            ProductResponseDto::description,
            ProductResponseDto::name,
            ProductResponseDto::price,
            ProductResponseDto::stockQuantity)
        .containsExactly(
            1L,
            productRequestDto.description(),
            productRequestDto.name(),
            productRequestDto.price(),
            productRequestDto.stockQuantity());
    assertThat(result.getBody().categories())
        .extracting(CategoryResponseDto::id)
        .containsExactlyInAnyOrderElementsOf(productRequestDto.categoryIds());
  }

  @Test
  void testDeleteProduct() {
    ResponseEntity<Void> result =
        testRestTemplate.exchange("/products/{productId}", HttpMethod.DELETE, null, Void.class, 1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(result.getBody()).isNull();
  }

  @Test
  void testGetProductReviews() {
    ResponseEntity<CustomPagedModel<ReviewResponseDto>> result =
        testRestTemplate.exchange(
            "/products/{productId}/reviews?page=0&size=25&sort=description,asc&rating=BAD",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {},
            1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent())
        .isNotNull()
        .hasSize(2)
        .extracting(ReviewResponseDto::description)
        .containsExactly("Product 1 is bad 1", "Product 1 is bad 2");
  }

  @Test
  void testCreateProductReview() {
    ResponseEntity<ReviewResponseDto> result =
        testRestTemplate.postForEntity(
            "/products/{productId}/reviews", reviewRequestDto, ReviewResponseDto.class, 1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getHeaders().getLocation()).hasPath("/products/1/reviews/6");
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            ReviewResponseDto::id,
            ReviewResponseDto::date,
            ReviewResponseDto::description,
            ReviewResponseDto::rating)
        .containsExactly(
            6L, LocalDate.now(), reviewRequestDto.description(), reviewRequestDto.rating());
  }

  @Test
  void testUpdateProductReview() {
    ResponseEntity<ReviewResponseDto> result =
        testRestTemplate.exchange(
            "/products/{productId}/reviews/{reviewId}",
            HttpMethod.PUT,
            new HttpEntity<>(reviewRequestDto),
            ReviewResponseDto.class,
            1L,
            1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            ReviewResponseDto::id,
            ReviewResponseDto::date,
            ReviewResponseDto::description,
            ReviewResponseDto::rating)
        .containsExactly(
            1L, LocalDate.now(), reviewRequestDto.description(), reviewRequestDto.rating());
  }

  @Test
  void testDeleteProductReview() {
    ResponseEntity<Void> result =
        testRestTemplate.exchange(
            "/products/{productId}/reviews/{reviewId}",
            HttpMethod.DELETE,
            null,
            Void.class,
            1L,
            1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(result.getBody()).isNull();
  }
}
