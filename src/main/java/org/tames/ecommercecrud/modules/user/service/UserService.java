package org.tames.ecommercecrud.modules.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.user.dto.UserResponseDto;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.exception.UserNotFoundException;
import org.tames.ecommercecrud.modules.user.mapper.UserMapper;
import org.tames.ecommercecrud.modules.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public UserResponseDto getUser(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return userMapper.toDto(user);
  }

  @Override
  public User loadUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new UserNotFoundException(
                    String.format("User with username: %s not found.", username)));
  }
}
