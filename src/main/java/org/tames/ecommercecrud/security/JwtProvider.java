package org.tames.ecommercecrud.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.entity.User;

@Component
public class JwtProvider {
  private final Algorithm accessTokenAlgorithm;
  private final Algorithm refreshTokenAlgorithm;
  private final ObjectMapper objectMapper;

  public JwtProvider(
      @Value("${jwt-public-key}") RSAPublicKey jwtPublicKey,
      @Value("${jwt-private-key}") RSAPrivateKey jwtPrivateKey,
      @Value("${refresh-token-public-key}") RSAPublicKey refreshTokenPublicKey,
      @Value("${refresh-token-private-key}") RSAPrivateKey refreshTokenPrivateKey,
      ObjectMapper objectMapper) {

    this.accessTokenAlgorithm = Algorithm.RSA256(jwtPublicKey, jwtPrivateKey);
    this.refreshTokenAlgorithm = Algorithm.RSA256(refreshTokenPublicKey, refreshTokenPrivateKey);
    this.objectMapper = objectMapper;
  }

  public DecodedJWT verifyAccessToken(String accessToken) throws JWTVerificationException {
    return verifyToken(accessToken, accessTokenAlgorithm);
  }

  public DecodedJWT verifyRefreshToken(String refreshToken) throws JWTVerificationException {
    return verifyToken(refreshToken, refreshTokenAlgorithm);
  }

  public String mintAccessToken(User user) {
    Instant now = Instant.now();
    try {
      return JWT.create()
          .withIssuedAt(now)
          .withIssuer("ecommerce-crud")
          .withExpiresAt(now.plus(15, ChronoUnit.MINUTES))
          .withSubject(objectMapper.writeValueAsString(user))
          .withClaim("roles", user.getRoles().stream().map(Role::toString).toList())
          .sign(accessTokenAlgorithm);
    } catch (JsonProcessingException ex) {
      throw new JWTCreationException("Couldn't serialize user", ex);
    }
  }

  public String mintRefreshToken(User user) {
    Instant now = Instant.now();
    return JWT.create()
        .withIssuedAt(now)
        .withIssuer("ecommerce-crud")
        .withExpiresAt(now.plus(30, ChronoUnit.DAYS))
        .withSubject(user.getId().toString())
        .sign(refreshTokenAlgorithm);
  }

  public User extractUser(DecodedJWT decodedJWT) {
    try {
      return objectMapper.readValue(decodedJWT.getSubject(), User.class);
    } catch (JsonProcessingException ex) {
      throw new JWTDecodeException("Couldn't decode token", ex);
    }
  }

  private DecodedJWT verifyToken(String token, Algorithm algorithm)
      throws JWTVerificationException {
    return JWT.require(algorithm).withIssuer("ecommerce-crud").build().verify(token);
  }
}
