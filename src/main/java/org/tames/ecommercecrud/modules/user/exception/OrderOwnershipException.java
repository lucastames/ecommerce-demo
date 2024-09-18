package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class OrderOwnershipException extends ErrorResponseException {
  public OrderOwnershipException(Long orderId, String username) {
    super(
        HttpStatus.FORBIDDEN,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            String.format("User %s doesn't own order with ID: %d", username, orderId)),
        null);
  }
}
