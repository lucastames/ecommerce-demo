package org.tames.ecommercecrud.annotations;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.RoleType;

public class WithMockAdminSecurityContextFactory
    implements WithSecurityContextFactory<WithMockAdmin> {
  @Override
  public SecurityContext createSecurityContext(WithMockAdmin annotation) {
    User adminUser = new User(annotation.email(), annotation.username(), null, null);
    adminUser.setId(1L);
    Role adminRole = new Role(RoleType.ADMIN);

    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(adminUser, null, List.of(adminRole));
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
