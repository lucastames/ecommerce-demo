package org.tames.ecommercecrud.modules.auth.controller;

import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tames.ecommercecrud.modules.auth.dto.LoginUserRequestDto;
import org.tames.ecommercecrud.modules.auth.dto.RegisterUserRequestDto;
import org.tames.ecommercecrud.modules.auth.service.AuthService;
import org.tames.ecommercecrud.modules.auth.service.AuthService.AuthTokens;

@RestController
@RequestMapping("auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("admin/register")
  public ResponseEntity<Void> registerAdmin(
      @RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
    AuthTokens authTokens = authService.registerAdmin(registerUserRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .headers(headers -> setCookies(headers, authTokens))
        .build();
  }

  @PostMapping("customer/register")
  public ResponseEntity<Void> registerCustomer(
      @RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
    AuthTokens authTokens = authService.registerCustomer(registerUserRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .headers(headers -> setCookies(headers, authTokens))
        .build();
  }

  @PostMapping("login")
  public ResponseEntity<Void> loginUser(
      @RequestBody @Valid LoginUserRequestDto loginUserRequestDto) {
    AuthTokens authTokens = authService.loginUser(loginUserRequestDto);

    return ResponseEntity.ok().headers(headers -> setCookies(headers, authTokens)).build();
  }

  @PostMapping("refresh")
  public ResponseEntity<Void> getAccessToken(@CookieValue("refreshToken") String refreshToken) {
    AuthTokens authTokens = authService.refreshTokens(refreshToken);

    return ResponseEntity.ok().headers(headers -> setCookies(headers, authTokens)).build();
  }

  private void setCookies(HttpHeaders headers, AuthTokens authTokens) {
    ResponseCookie accessTokenCookie =
        ResponseCookie.from("accessToken", authTokens.accessToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .maxAge(Duration.ofMinutes(15))
            .path("/")
            .build();

    ResponseCookie refreshTokenCookie =
        ResponseCookie.from("refreshToken", authTokens.refreshToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .maxAge(Duration.ofDays(30))
            .path("/auth/refresh")
            .build();

    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
  }
}
