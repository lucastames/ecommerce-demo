package org.tames.ecommercecrud.modules.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ReviewOwnershipException extends ErrorResponseException {
  public ReviewOwnershipException(Long reviewId, String username) {
    this(String.format("User %s isn't the owner of review with ID: %d", username, reviewId));
  }

  public ReviewOwnershipException(String detail) {
    super(
        HttpStatus.FORBIDDEN, ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, detail), null);
  }
}
