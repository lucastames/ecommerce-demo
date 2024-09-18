package org.tames.ecommercecrud.modules.user.entity;

import jakarta.persistence.*;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.tames.ecommercecrud.modules.user.enums.RoleType;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType roleType;

  public Role() {}

  public Role(String roleType) {
    this(RoleType.valueOf(roleType));
  }

  public Role(RoleType roleType) {
    this.roleType = roleType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public RoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(RoleType roleType) {
    this.roleType = roleType;
  }

  @Override
  public String getAuthority() {
    return "ROLE_" + roleType.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role role)) return false;
    return id != null && Objects.equals(id, role.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
