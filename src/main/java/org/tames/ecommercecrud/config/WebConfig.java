package org.tames.ecommercecrud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
    return p -> {
      p.setFallbackPageable(PageRequest.of(0, 50));
      p.setMaxPageSize(50);
    };
  }
}
