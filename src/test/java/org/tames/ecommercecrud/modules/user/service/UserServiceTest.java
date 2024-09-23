package org.tames.ecommercecrud.modules.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tames.ecommercecrud.modules.user.dto.UserResponseDto;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.exception.UserNotFoundException;
import org.tames.ecommercecrud.modules.user.mapper.UserMapper;
import org.tames.ecommercecrud.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock UserRepository userRepository;
  @Mock UserMapper userMapper;

  @InjectMocks UserService userService;

  UserResponseDto userResponseDto;
  User user;

  @BeforeEach
  void setUp() {
    user = new User("email@emal.com", "username", "password", "123456");
    user.setId(1L);
    userResponseDto = new UserResponseDto(1L, "email@emal.com", "username", "123456");
  }

  @Test
  void testGetUser_WhenExistingUserIdIsProvided_ShouldReturnUserResponseDto() {
    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userResponseDto);

    UserResponseDto result = userService.getUser(1L);

    assertThat(result)
        .isNotNull()
        .extracting(
            UserResponseDto::id,
            UserResponseDto::email,
            UserResponseDto::phoneNumber,
            UserResponseDto::username)
        .containsExactly(
            userResponseDto.id(),
            userResponseDto.email(),
            userResponseDto.phoneNumber(),
            userResponseDto.username());
  }

  @Test
  void testGetUser_WhenNonExistingUserIdIsProvided_ShouldThrowUserNotFoundException() {
    given(userRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUser(99L)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testLoadUserByUsername_WhenExistingUsernameIsProvided_ShoudReturnUserResponseDto() {
    given(userRepository.findByUsername("username")).willReturn(Optional.of(user));

    User result = userService.loadUserByUsername("username");

    assertThat(result)
        .isNotNull()
        .extracting(User::getId, User::getEmail, User::getPhoneNumber, User::getUsername)
        .containsExactly(user.getId(), user.getEmail(), user.getPhoneNumber(), user.getUsername());
  }

  @Test
  void testLoadUserByUsername_WhenNonExistingUsernameIsProvided_ShoudThrowUserNotFoundException() {
    given(userRepository.findByUsername("nonExistingUsername")).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.loadUserByUsername("nonExistingUsername"))
        .isInstanceOf(UserNotFoundException.class);
  }
}
