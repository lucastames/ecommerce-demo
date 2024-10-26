package org.tames.ecommercecrud.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.tames.ecommercecrud.security.JwtProvider;
import org.tames.ecommercecrud.security.SecurityConfig;

@TestConfiguration
@Import(SecurityConfig.class)
public class ControllerTestConfig {
  @MockBean(name = "jwtProvider")
  JwtProvider jwtProvider;

  @MockBean(name = "userDetailsService")
  UserDetailsService userDetailsService;
}
