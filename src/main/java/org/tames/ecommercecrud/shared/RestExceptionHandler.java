package org.tames.ecommercecrud.shared;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  private record ErrorDetails(String field, String detail) {}

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
