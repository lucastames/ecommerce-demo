package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EmailAlreadyExistsException extends ErrorResponseException {
  public EmailAlreadyExistsException(String email) {
    super(
        HttpStatus.CONFLICT,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, String.format("Email already exists: %s", email)),
        null);
  }
}
