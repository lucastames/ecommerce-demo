package org.tames.ecommercecrud.modules.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.enums.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleType(RoleType roleType);
}
