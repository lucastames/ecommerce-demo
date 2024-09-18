package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ShippingAddressOwnershipException extends ErrorResponseException {
  public ShippingAddressOwnershipException(Long shippingAddressId, String username) {
    super(
        HttpStatus.FORBIDDEN,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            String.format(
                "User %s isn't the owner of shipping address with ID: %d",
                username, shippingAddressId)),
        null);
  }
}
