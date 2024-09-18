package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UserNotFoundException extends ErrorResponseException {
  public UserNotFoundException(Long id) {
    this(String.format("User not found with ID: %d", id), null);
  }

  public UserNotFoundException(String username) {
    this(String.format("User not found with username: %s", username), null);
  }

  public UserNotFoundException(String detail, Throwable cause) {
    super(
        HttpStatus.NOT_FOUND,
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail),
        cause);
  }
}
