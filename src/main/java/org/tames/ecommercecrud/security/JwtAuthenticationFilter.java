package org.tames.ecommercecrud.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.entity.User;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;
  private final HandlerExceptionResolver handlerExceptionResolver;

  public JwtAuthenticationFilter(
      JwtProvider jwtProvider, HandlerExceptionResolver handlerExceptionResolver) {
    this.jwtProvider = jwtProvider;
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {

      Cookie[] cookies = request.getCookies();

      if (cookies != null) {
        Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals("accessToken"))
            .findFirst()
            .map(Cookie::getValue)
            .ifPresent(
                accessToken -> {
                  DecodedJWT accessTokenClaims = jwtProvider.verifyAccessToken(accessToken);
                  List<Role> roles = accessTokenClaims.getClaim("roles").asList(Role.class);
                  User user =
                      jwtProvider.extractUser(accessTokenClaims); // Only id, email, username

                  Authentication authentication =
                      UsernamePasswordAuthenticationToken.authenticated(user, null, roles);

                  SecurityContext securityContext = new SecurityContextImpl(authentication);
                  SecurityContextHolder.setContext(securityContext);
                });
      }

      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }
  }
}
