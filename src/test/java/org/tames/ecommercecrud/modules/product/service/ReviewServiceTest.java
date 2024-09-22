package org.tames.ecommercecrud.modules.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.entity.Review;
import org.tames.ecommercecrud.modules.product.enums.Rating;
import org.tames.ecommercecrud.modules.product.exception.ReviewDoesNotBelongToProductException;
import org.tames.ecommercecrud.modules.product.exception.ReviewNotFoundException;
import org.tames.ecommercecrud.modules.product.exception.ReviewOwnershipException;
import org.tames.ecommercecrud.modules.product.mapper.ReviewMapper;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.product.repository.ReviewRepository;
import org.tames.ecommercecrud.modules.product.specification.ReviewSpecs.ReviewFilter;
import org.tames.ecommercecrud.modules.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
  @Mock ReviewRepository reviewRepository;
  @Mock ReviewMapper reviewMapper;
  @Mock ProductRepository productRepository;

  @InjectMocks ReviewService reviewService;

  User user;
  Product product;
  SaveReviewRequestDto reviewRequestDto;
  Review mappedReview;
  Review persistedReview;
  ReviewResponseDto reviewResponseDto;

  @BeforeEach
  void setUp() {
    user = new User("email@email.com", "username", "password", "123456");
    user.setId(1L);

    product = new Product("Product", BigDecimal.valueOf(10.00), "Description", 10);
    product.setId(1L);

    reviewRequestDto = new SaveReviewRequestDto("Review desc 1", Rating.GOOD);

    mappedReview = new Review("Review desc 1", LocalDate.now(), Rating.GOOD, product, user);

    reviewResponseDto =
        new ReviewResponseDto(1L, "Review desc 1", LocalDate.now(), Rating.GOOD, "Author");

    persistedReview = new Review("Review desc 1", LocalDate.now(), Rating.GOOD, product, user);
    persistedReview.setId(1L);
  }

  @Test
  void
      testGetProductReviews_WhenPageParamsAndFilterAreProvided_ShouldReturnRelatedProductReviews() {
    Pageable pageable = PageRequest.of(0, 50);
    ReviewFilter reviewFilter = new ReviewFilter("description", LocalDate.now(), Rating.GOOD);

    given(reviewRepository.findAll(any(Specification.class), eq(pageable)))
        .willReturn(new PageImpl<>(List.of(persistedReview)));
    given(reviewMapper.toDto(persistedReview)).willReturn(reviewResponseDto);

    PagedModel<ReviewResponseDto> result = reviewService.getReviews(1L, pageable, reviewFilter);

    assertThat(result).isNotNull();
    assertThat(result.getContent())
        .isNotNull()
        .hasSize(1)
        .first()
        .extracting(
            ReviewResponseDto::id,
            ReviewResponseDto::description,
            ReviewResponseDto::rating,
            ReviewResponseDto::date,
            ReviewResponseDto::author)
        .containsExactly(
            reviewResponseDto.id(),
            reviewResponseDto.description(),
            reviewResponseDto.rating(),
            reviewResponseDto.date(),
            reviewResponseDto.author());
  }

  @Test
  void testCreateReview_WhenAllParametersAreProvided_ShouldReturnCreatedReviewResponseDto() {
    given(reviewMapper.toEntity(reviewRequestDto, user, product)).willReturn(mappedReview);
    given(productRepository.getReferenceById(1L)).willReturn(product);
    given(reviewRepository.save(mappedReview)).willReturn(persistedReview);
    given(reviewMapper.toDto(persistedReview)).willReturn(reviewResponseDto);

    ReviewResponseDto result = reviewService.createReview(1L, user, reviewRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            ReviewResponseDto::id,
            ReviewResponseDto::description,
            ReviewResponseDto::rating,
            ReviewResponseDto::date,
            ReviewResponseDto::author)
        .containsExactly(
            reviewResponseDto.id(),
            reviewResponseDto.description(),
            reviewResponseDto.rating(),
            reviewResponseDto.date(),
            reviewResponseDto.author());
  }

  @Test
  void testUpdatedReview_WhenAllParametersAreProvided_ShouldReturnUpdatedReviewResponseDto() {
    ReviewResponseDto updatedReviewResponseDto =
        new ReviewResponseDto(
            1L, "Description updated", LocalDate.parse("2024-12-20"), Rating.GOOD, "Author");
    SaveReviewRequestDto updatedReviewRequestDto =
        new SaveReviewRequestDto("Description updated", Rating.GOOD);
    Review updatedReview =
        new Review(
            "Description updated", LocalDate.parse("2024-12-20"), Rating.GOOD, product, user);

    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));
    given(reviewRepository.save(persistedReview)).willReturn(updatedReview);
    given(reviewMapper.toDto(updatedReview)).willReturn(updatedReviewResponseDto);

    ReviewResponseDto result = reviewService.updateReview(updatedReviewRequestDto, 1L, 1L, user);

    assertThat(result)
        .isNotNull()
        .extracting(
            ReviewResponseDto::id,
            ReviewResponseDto::description,
            ReviewResponseDto::rating,
            ReviewResponseDto::date,
            ReviewResponseDto::author)
        .containsExactly(
            updatedReviewResponseDto.id(),
            updatedReviewResponseDto.description(),
            updatedReviewResponseDto.rating(),
            updatedReviewResponseDto.date(),
            updatedReviewResponseDto.author());
  }

  @Test
  void testUpdateReview_WhenNonExistingReviewIdIsProvided_ShouldThrowReviewNotFoundException() {
    given(reviewRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.updateReview(reviewRequestDto, 1L, 99L, user))
        .isInstanceOf(ReviewNotFoundException.class);
  }

  @Test
  void testUpdateReview_WhenReviewDoesntBelongToCurrentUser_ShouldThrowReviewOwnershipException() {
    User otherUser = new User("email2@email.com", "username2", "password2", "12345");
    otherUser.setId(2L);

    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));

    assertThatThrownBy(() -> reviewService.updateReview(reviewRequestDto, 1L, 1L, otherUser))
        .isInstanceOf(ReviewOwnershipException.class);
  }

  @Test
  void
      testUpdateReview_WhenReviewDoesntBelongToProduct_ShouldThrowReviewDoesNotBelongToProductException() {
    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));

    assertThatThrownBy(() -> reviewService.updateReview(reviewRequestDto, 99L, 1L, user))
        .isInstanceOf(ReviewDoesNotBelongToProductException.class);
  }

  @Test
  void testDeleteReview_WhenAllParametersAreProvided_ShouldDeleteReview() {
    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));

    reviewService.deleteReview(1L, 1L, user);

    then(reviewRepository).should().delete(persistedReview);
  }

  @Test
  void testDeleteReview_WhenNonExistingReviewIdIsProvided_ShouldThrowReviewNotFoundException() {
    given(reviewRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.deleteReview(1L, 99L, user))
        .isInstanceOf(ReviewNotFoundException.class);
  }

  @Test
  void testDeleteReview_WhenReviewDoesntBelongToCurrentUser_ShouldThrowReviewOwnershipException() {
    User otherUser = new User("email2@email.com", "username2", "password2", "12345");
    otherUser.setId(2L);

    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));

    assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L, otherUser))
        .isInstanceOf(ReviewOwnershipException.class);
  }

  @Test
  void
      testDeleteReview_WhenReviewDoesntBelongToProduct_ShouldThrowReviewDoesNotBelongToProductException() {
    given(reviewRepository.findById(1L)).willReturn(Optional.of(persistedReview));

    assertThatThrownBy(() -> reviewService.deleteReview(99L, 1L, user))
        .isInstanceOf(ReviewDoesNotBelongToProductException.class);
  }
}
