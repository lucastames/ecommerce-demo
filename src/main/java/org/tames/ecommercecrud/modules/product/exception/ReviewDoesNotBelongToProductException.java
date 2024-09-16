package org.tames.ecommercecrud.modules.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ReviewDoesNotBelongToProductException extends ErrorResponseException {
  public ReviewDoesNotBelongToProductException(Long reviewId, Long productId) {
    this(
        String.format(
            "Review with ID: %d doesn't belong to product with ID: %d", reviewId, productId));
  }

  public ReviewDoesNotBelongToProductException(String detail) {
    super(
        HttpStatus.BAD_REQUEST,
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail),
        null);
  }
}
