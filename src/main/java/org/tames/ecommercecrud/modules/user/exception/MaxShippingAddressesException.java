package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class MaxShippingAddressesException extends ErrorResponseException {
  public MaxShippingAddressesException(Integer max, String username) {
    super(
        HttpStatus.CONFLICT,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            String.format("User %s cannot have more than %d shipping addresses", username, max)),
        null);
  }
}
