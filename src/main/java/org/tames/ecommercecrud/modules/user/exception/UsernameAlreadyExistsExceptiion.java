package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UsernameAlreadyExistsExceptiion extends ErrorResponseException {
  public UsernameAlreadyExistsExceptiion(String username) {
    super(
        HttpStatus.CONFLICT,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, String.format("Username already exists: %s", username)),
        null);
  }
}
