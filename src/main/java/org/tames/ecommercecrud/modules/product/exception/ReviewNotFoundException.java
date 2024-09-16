package org.tames.ecommercecrud.modules.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ReviewNotFoundException extends ErrorResponseException {
  public ReviewNotFoundException(Long id) {
    this(String.format("Review with ID: %d not found.", id));
  }

  public ReviewNotFoundException(String detail) {
    super(
        HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail), null);
  }
}
