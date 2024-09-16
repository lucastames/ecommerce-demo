package org.tames.ecommercecrud.modules.product.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.product.dto.ReviewResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveReviewRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.entity.Review;
import org.tames.ecommercecrud.modules.user.entity.User;

@Component
public class ReviewMapper {
  public Review toEntity(SaveReviewRequestDto saveReviewRequestDto, User user, Product product) {
    return new Review(
        saveReviewRequestDto.description(),
        LocalDate.now(),
        saveReviewRequestDto.rating(),
        product,
        user);
  }

  public ReviewResponseDto toDto(Review review) {
    return new ReviewResponseDto(
        review.getId(),
        review.getDescription(),
        review.getDate(),
        review.getRating(),
        review.getUser().getUsername());
  }

  public void updateFromDto(Review review, SaveReviewRequestDto saveReviewRequestDto) {
    review.setDate(LocalDate.now());
    review.setDescription(saveReviewRequestDto.description());
    review.setRating(saveReviewRequestDto.rating());
  }
}
