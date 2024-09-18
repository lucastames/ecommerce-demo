package org.tames.ecommercecrud.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "app_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 255)
  @NotEmpty
  @Email
  @Column(unique = true)
  private String email;

  @Size(max = 127)
  @NotEmpty
  @Column(unique = true)
  private String username;

  @NotEmpty
  @Size(max = 127)
  @JsonIgnore
  private String password;

  @NotEmpty @JsonIgnore private String phoneNumber;

  @ManyToMany
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<ShippingAddress> shippingAddresses = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Order> orders = new ArrayList<>();

  public User() {}

  public User(String email, String username, String password, String phoneNumber) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.phoneNumber = phoneNumber;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void addRole(Role role) {
    roles.add(role);
  }

  public void removeRole(Role role) {
    roles.remove(role);
  }

  public List<ShippingAddress> getShippingAddresses() {
    return shippingAddresses;
  }

  public void addShippingAddress(ShippingAddress shippingAddress) {
    shippingAddresses.add(shippingAddress);
  }

  public void removeShippingAddress(ShippingAddress shippingAddress) {
    shippingAddresses.remove(shippingAddress);
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void addOrder(Order order) {
    orders.add(order);
  }

  public void removeOrder(Order order) {
    orders.remove(order);
  }

  @Override
  public Set<Role> getAuthorities() {
    return roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    return id != null && Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
