package org.tames.ecommercecrud.modules.product.service;

import static org.tames.ecommercecrud.modules.product.specification.ReviewSpecs.byProductId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Review;
import org.tames.ecommercecrud.modules.product.exception.ReviewDoesNotBelongToProductException;
import org.tames.ecommercecrud.modules.product.exception.ReviewNotFoundException;
import org.tames.ecommercecrud.modules.product.exception.ReviewOwnershipException;
import org.tames.ecommercecrud.modules.product.mapper.ReviewMapper;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.product.repository.ReviewRepository;
import org.tames.ecommercecrud.modules.product.specification.ReviewSpecs.ReviewFilter;
import org.tames.ecommercecrud.modules.user.entity.User;

@Service
@Transactional(readOnly = true)
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final ProductRepository productRepository;
  private final ReviewMapper reviewMapper;

  public ReviewService(
      ReviewRepository reviewRepository,
      ProductRepository productRepository,
      ReviewMapper reviewMapper) {
    this.reviewRepository = reviewRepository;
    this.productRepository = productRepository;
    this.reviewMapper = reviewMapper;
  }

  public PagedModel<ReviewResponseDto> getReviews(
      Long productId, Pageable pageable, ReviewFilter reviewFilter) {
    Page<ReviewResponseDto> reviewPage =
        reviewRepository
            .findAll(reviewFilter.and(byProductId(productId)), pageable)
            .map(reviewMapper::toDto);

    return new PagedModel<>(reviewPage);
  }

  @Transactional
  public ReviewResponseDto createReview(
      Long productId, User user, SaveReviewRequestDto saveReviewRequestDto) {
    Review review =
        reviewMapper.toEntity(
            saveReviewRequestDto, user, productRepository.getReferenceById(productId));

    return reviewMapper.toDto(reviewRepository.save(review));
  }

  @Transactional
  public ReviewResponseDto updateReview(
      SaveReviewRequestDto saveReviewRequestDto, Long productId, Long reviewId, User user) {
    Review review = getAndValidateReview(reviewId, productId, user);

    reviewMapper.updateFromDto(review, saveReviewRequestDto);
    return reviewMapper.toDto(reviewRepository.save(review));
  }

  @Transactional
  public void deleteReview(Long productId, Long reviewId, User user) {
    Review review = getAndValidateReview(reviewId, productId, user);
    reviewRepository.delete(review);
  }

  private Review getAndValidateReview(Long reviewId, Long productId, User user) {
    Review review =
        reviewRepository
            .findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

    if (!review.getUser().getId().equals(user.getId())) {
      throw new ReviewOwnershipException(reviewId, user.getUsername());
    }

    if (!review.getProduct().getId().equals(productId)) {
      throw new ReviewDoesNotBelongToProductException(reviewId, productId);
    }

    return review;
  }
}
