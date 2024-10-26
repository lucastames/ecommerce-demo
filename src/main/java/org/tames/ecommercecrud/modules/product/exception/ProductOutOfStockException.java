package org.tames.ecommercecrud.modules.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ProductOutOfStockException extends ErrorResponseException {
  public ProductOutOfStockException(Long productId) {
    super(
        HttpStatus.CONFLICT,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, String.format("Product with ID: %d is out of stock.", productId)),
        null);
  }
}
