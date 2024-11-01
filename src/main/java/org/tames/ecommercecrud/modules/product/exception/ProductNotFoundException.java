package org.tames.ecommercecrud.modules.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ProductNotFoundException extends ErrorResponseException {
  public ProductNotFoundException(Long id) {
    this(String.format("Product with ID: %d not found.", id));
  }

  public ProductNotFoundException(String detail) {
    super(
        HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail), null);
  }
}
