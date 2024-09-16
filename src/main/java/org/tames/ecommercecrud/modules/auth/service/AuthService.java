package org.tames.ecommercecrud.modules.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.auth.dto.LoginUserRequestDto;
import org.tames.ecommercecrud.modules.auth.dto.RegisterUserRequestDto;
import org.tames.ecommercecrud.modules.user.entity.Role;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.RoleType;
import org.tames.ecommercecrud.modules.user.exception.EmailAlreadyExistsException;
import org.tames.ecommercecrud.modules.user.exception.RoleNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.UserNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.UsernameAlreadyExistsExceptiion;
import org.tames.ecommercecrud.modules.user.repository.RoleRepository;
import org.tames.ecommercecrud.modules.user.repository.UserRepository;
import org.tames.ecommercecrud.security.JwtProvider;

@Service
@Transactional(readOnly = true)
public class AuthService {
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  public AuthService(
      JwtProvider jwtProvider,
      UserRepository userRepository,
      RoleRepository roleRepository,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder) {
    this.jwtProvider = jwtProvider;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public AuthTokens registerAdmin(RegisterUserRequestDto registerUserDto) {
    return registerUser(
        registerUserDto,
        Set.of(
            roleRepository
                .findByRoleType(RoleType.ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(RoleType.ADMIN))));
  }

  @Transactional
  public AuthTokens registerCustomer(RegisterUserRequestDto registerUserDto) {
    return registerUser(
        registerUserDto,
        Set.of(
            roleRepository
                .findByRoleType(RoleType.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException(RoleType.CUSTOMER))));
  }

  public AuthTokens loginUser(LoginUserRequestDto loginUserRequestDto) {
    Authentication authenticationRequest =
        UsernamePasswordAuthenticationToken.unauthenticated(
            loginUserRequestDto.username(), loginUserRequestDto.password());

    Authentication authenticationResponse =
        authenticationManager.authenticate(authenticationRequest);

    return generateAuthTokens((User) authenticationResponse.getPrincipal());
  }

  public AuthTokens refreshTokens(String refreshToken) {
    DecodedJWT tokenClaims = jwtProvider.verifyRefreshToken(refreshToken);
    Long userId = Long.parseLong(tokenClaims.getSubject());
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    return generateAuthTokens(user);
  }

  @Transactional
  protected AuthTokens registerUser(RegisterUserRequestDto registerUserDto, Set<Role> roles) {
    if (userRepository.existsByEmail(registerUserDto.email())) {
      throw new EmailAlreadyExistsException(registerUserDto.email());
    }

    if (userRepository.existsByUsername(registerUserDto.username())) {
      throw new UsernameAlreadyExistsExceptiion(registerUserDto.username());
    }

    User user =
        new User(
            registerUserDto.email(),
            registerUserDto.username(),
            passwordEncoder.encode(registerUserDto.password()),
            registerUserDto.phoneNumber());
    roles.forEach(user::addRole);

    User createdUser = userRepository.save(user);
    return generateAuthTokens(createdUser);
  }

  private AuthTokens generateAuthTokens(User user) {
    return new AuthTokens(jwtProvider.mintAccessToken(user), jwtProvider.mintRefreshToken(user));
  }

  public record AuthTokens(String accessToken, String refreshToken) {}
}
