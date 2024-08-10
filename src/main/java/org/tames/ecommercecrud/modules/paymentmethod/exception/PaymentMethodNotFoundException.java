package org.tames.ecommercecrud.modules.paymentmethod.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class PaymentMethodNotFoundException extends ErrorResponseException {
  public PaymentMethodNotFoundException(Long id) {
    this(String.format("Payment method with ID %d not found.", id));
  }

  public PaymentMethodNotFoundException(String detail) {
    super(
        HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail), null);
  }
}
