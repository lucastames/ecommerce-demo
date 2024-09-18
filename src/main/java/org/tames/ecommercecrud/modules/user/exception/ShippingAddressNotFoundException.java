package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ShippingAddressNotFoundException extends ErrorResponseException {
  public ShippingAddressNotFoundException(Long shippingAddressId) {
    this(String.format("Shipping address with ID: %d not found.", shippingAddressId));
  }

  public ShippingAddressNotFoundException(String detail) {
    super(
        HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail), null);
  }
}
