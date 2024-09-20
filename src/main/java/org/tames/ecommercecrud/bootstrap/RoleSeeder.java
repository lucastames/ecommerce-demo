package org.tames.ecommercecrud.bootstrap;

import java.util.Arrays;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.enums.RoleType;
import org.tames.ecommercecrud.modules.user.repository.RoleRepository;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
  private final RoleRepository roleRepository;

  public RoleSeeder(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    loadRoles();
  }

  private void loadRoles() {
    Arrays.stream(RoleType.values())
        .filter(roleType -> roleRepository.findByRoleType(roleType).isEmpty())
        .forEach(roleType -> roleRepository.save(new Role(roleType)));
  }
}
