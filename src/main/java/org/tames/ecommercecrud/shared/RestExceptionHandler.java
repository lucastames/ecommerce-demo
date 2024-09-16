package org.tames.ecommercecrud.shared;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  private record ErrorDetails(String field, String detail) {}

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "Data integrity violation."),
        HttpHeaders.EMPTY,
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  @ExceptionHandler(JWTVerificationException.class)
  public ResponseEntity<Object> handleJWTVerificationException(
      JWTVerificationException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()),
        HttpHeaders.EMPTY,
        HttpStatus.UNAUTHORIZED,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String detail = "Failed to read request.";

    if (ex.getCause() instanceof InvalidFormatException formatException) {
      if (formatException.getTargetType() != null && formatException.getTargetType().isEnum()) {
        detail =
            String.format(
                "provided value '%s' is invalid for field '%s'. It must be one of the following values: %s.",
                formatException.getValue(),
                formatException.getPath().getLast().getFieldName(),
                Arrays.toString(formatException.getTargetType().getEnumConstants()));
      }
    }

    ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    List<ErrorDetails> errorDetailsList =
        ex.getFieldErrors().stream()
            .map(e -> new ErrorDetails(e.getField(), e.getDefaultMessage()))
            .toList();
    ex.getBody().setProperty("errors", errorDetailsList);

    return super.handleMethodArgumentNotValid(ex, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> createResponseEntity(
      Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
    if (body instanceof ProblemDetail problemDetail) {
      problemDetail.setProperty("timestamp", Instant.now().toString());
    }

    return super.createResponseEntity(body, headers, statusCode, request);
  }
}
