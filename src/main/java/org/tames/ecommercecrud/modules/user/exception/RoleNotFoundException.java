package org.tames.ecommercecrud.modules.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.tames.ecommercecrud.modules.user.enums.RoleType;

public class RoleNotFoundException extends ErrorResponseException {
  public RoleNotFoundException(RoleType roleType) {
    super(
        HttpStatus.NOT_FOUND,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, String.format("Role not found with role type: %s", roleType)),
        null);
  }
}
