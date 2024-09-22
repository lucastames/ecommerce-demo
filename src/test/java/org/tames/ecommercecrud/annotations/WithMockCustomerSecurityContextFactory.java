package org.tames.ecommercecrud.annotations;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.RoleType;

import java.util.List;

public class WithMockCustomerSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomer> {
  @Override
  public SecurityContext createSecurityContext(WithMockCustomer annotation) {
    User customerUser = new User(annotation.email(), annotation.username(), null, null);
    customerUser.setId(1L);
    Role customerRole = new Role(RoleType.CUSTOMER);

    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(
            customerUser, null, List.of(customerRole));
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
