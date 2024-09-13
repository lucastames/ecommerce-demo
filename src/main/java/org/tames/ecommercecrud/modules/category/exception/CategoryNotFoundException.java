package org.tames.ecommercecrud.modules.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class CategoryNotFoundException extends ErrorResponseException {
  public CategoryNotFoundException(Long id) {
    this(String.format("Category with ID: %d not found.", id));
  }

  public CategoryNotFoundException(String detail) {
    super(
        HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail), null);
  }
}
