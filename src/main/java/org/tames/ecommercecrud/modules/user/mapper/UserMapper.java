package org.tames.ecommercecrud.modules.user.mapper;

import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.user.dto.UserResponseDto;
import org.tames.ecommercecrud.modules.user.entity.User;

@Component
public class UserMapper {
  public UserResponseDto toDto(User user) {
    return new UserResponseDto(
        user.getId(), user.getEmail(), user.getUsername(), user.getPhoneNumber());
  }
}
