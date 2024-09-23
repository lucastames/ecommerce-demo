package org.tames.ecommercecrud.modules.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.user.dto.SaveShippingAddressRequestDto;
import org.tames.ecommercecrud.modules.user.dto.ShippingAddressResponseDto;
import org.tames.ecommercecrud.modules.user.entity.ShippingAddress;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.exception.MaxShippingAddressesException;
import org.tames.ecommercecrud.modules.user.exception.ShippingAddressNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.ShippingAddressOwnershipException;
import org.tames.ecommercecrud.modules.user.mapper.ShippingAddressMapper;
import org.tames.ecommercecrud.modules.user.repository.ShippingAddressRepository;

@ExtendWith(MockitoExtension.class)
public class ShippingAddressServiceTest {
  @Mock ShippingAddressRepository shippingAddressRepository;
  @Mock ShippingAddressMapper shippingAddressMapper;

  @InjectMocks ShippingAddressService shippingAddressService;

  ShippingAddressResponseDto shippingAddressResponseDto;
  SaveShippingAddressRequestDto shippingAddressRequestDto;
  ShippingAddress mappedShippingAddress;
  ShippingAddress persistedShippingAddress;
  User user;

  @BeforeEach
  void setUp() {
    user = new User("email@email.com", "username", "password", "123456789");
    user.setId(1L);

    shippingAddressRequestDto =
        new SaveShippingAddressRequestDto("Address 1", "Addition info", "City", "12345");

    mappedShippingAddress =
        new ShippingAddress("Address 1", "Addition info", "City", "12345", user);

    shippingAddressResponseDto =
        new ShippingAddressResponseDto(1L, "Address 1", "Addition info", "City", "12345");

    persistedShippingAddress =
        new ShippingAddress("Address 1", "Addition info", "City", "12345", user);
    persistedShippingAddress.setId(1L);
  }

  @Test
  void testGetUserShippingAddresses_WhenUserIsProvided_ShouldReturnShippingAddressesFromThatUser() {
    given(shippingAddressRepository.findAll(any(Specification.class)))
        .willReturn(List.of(persistedShippingAddress));
    given(shippingAddressMapper.toDto(persistedShippingAddress))
        .willReturn(shippingAddressResponseDto);

    List<ShippingAddressResponseDto> result = shippingAddressService.getUserShippingAddresses(user);

    assertThat(result)
        .isNotNull()
        .hasSize(1)
        .first()
        .extracting(
            ShippingAddressResponseDto::id,
            ShippingAddressResponseDto::address,
            ShippingAddressResponseDto::additionalInfo,
            ShippingAddressResponseDto::city,
            ShippingAddressResponseDto::postalCode)
        .containsExactly(
            shippingAddressResponseDto.id(),
            shippingAddressResponseDto.address(),
            shippingAddressResponseDto.additionalInfo(),
            shippingAddressResponseDto.city(),
            shippingAddressResponseDto.postalCode());
  }

  @Test
  void
      testCreateUserShippingAddress_WhenUserDidntReachMaxShippingAddressesYet_ShouldReturnCreatedShippingAddress() {
    given(shippingAddressRepository.count(any(Specification.class))).willReturn(0L);
    given(shippingAddressMapper.toEntity(shippingAddressRequestDto, user))
        .willReturn(mappedShippingAddress);
    given(shippingAddressRepository.save(mappedShippingAddress))
        .willReturn(persistedShippingAddress);
    given(shippingAddressMapper.toDto(persistedShippingAddress))
        .willReturn(shippingAddressResponseDto);

    ShippingAddressResponseDto result =
        shippingAddressService.createUserShippingAddress(shippingAddressRequestDto, user);

    Assertions.assertThat(result)
        .isNotNull()
        .extracting(
            ShippingAddressResponseDto::id,
            ShippingAddressResponseDto::address,
            ShippingAddressResponseDto::additionalInfo,
            ShippingAddressResponseDto::city,
            ShippingAddressResponseDto::postalCode)
        .containsExactly(
            shippingAddressResponseDto.id(),
            shippingAddressResponseDto.address(),
            shippingAddressResponseDto.additionalInfo(),
            shippingAddressResponseDto.city(),
            shippingAddressResponseDto.postalCode());
  }

  @Test
  void
      testCreateUserShippingAddress_WhenUserReachedMaxShippingAddresses_ShoulThrowMaxShippingAddressesException() {
    given(shippingAddressRepository.count(any(Specification.class))).willReturn(5L);

    assertThatThrownBy(
            () -> shippingAddressService.createUserShippingAddress(shippingAddressRequestDto, user))
        .isInstanceOf(MaxShippingAddressesException.class);
  }

  @Test
  void
      testUpdateUserShippingAddress_WhenExistingShippingAddressIdIsProvided_ShouldReturnUpdatedShippnigAddress() {
    SaveShippingAddressRequestDto updatedShippingAddressRequestDto =
        new SaveShippingAddressRequestDto("Address 2", "Additional info 2", "City 2", "123456");
    ShippingAddress updatedShippingAddress =
        new ShippingAddress("Address 2", "Additional info 2", "City 2", "123456", user);
    ShippingAddressResponseDto updatedShippingAddressResponseDto =
        new ShippingAddressResponseDto(1L, "Address 2", "Additional info 2", "City 2", "123456");

    given(shippingAddressRepository.findById(1L)).willReturn(Optional.of(persistedShippingAddress));
    given(shippingAddressRepository.save(persistedShippingAddress))
        .willReturn(updatedShippingAddress);
    given(shippingAddressMapper.toDto(updatedShippingAddress))
        .willReturn(updatedShippingAddressResponseDto);

    ShippingAddressResponseDto result =
        shippingAddressService.updateUserShippingAddress(
            updatedShippingAddressRequestDto, 1L, user);

    then(shippingAddressMapper)
        .should()
        .updateFromDto(persistedShippingAddress, updatedShippingAddressRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            ShippingAddressResponseDto::id,
            ShippingAddressResponseDto::address,
            ShippingAddressResponseDto::additionalInfo,
            ShippingAddressResponseDto::city,
            ShippingAddressResponseDto::postalCode)
        .containsExactly(
            updatedShippingAddressResponseDto.id(),
            updatedShippingAddressResponseDto.address(),
            updatedShippingAddressResponseDto.additionalInfo(),
            updatedShippingAddressResponseDto.city(),
            updatedShippingAddressResponseDto.postalCode());
  }

  @Test
  void
      testUpdateUserShippingAddress_WhenNonExistingShippingAddressIdIsProvided_ShouldThrowShippingAddressNotFoundException() {
    given(shippingAddressRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                shippingAddressService.updateUserShippingAddress(
                    shippingAddressRequestDto, 99L, user))
        .isInstanceOf(ShippingAddressNotFoundException.class);
  }

  @Test
  void
      testUpdateUserShippingAddress_WhenShippingAddressIsNotOwnedByCurrentUser_ShouldThrowShippingAddressOwnershipException() {
    User otherUser = new User("email@email2.com", "username2", "password2", "123456789");
    otherUser.setId(2L);

    given(shippingAddressRepository.findById(1L)).willReturn(Optional.of(persistedShippingAddress));

    assertThatThrownBy(
            () ->
                shippingAddressService.updateUserShippingAddress(
                    shippingAddressRequestDto, 1L, otherUser))
        .isInstanceOf(ShippingAddressOwnershipException.class);
  }

  @Test
  void
      testDeleteUserShippingAddresss_WhenExistingShippingAddressIdIsProvided_ShouldDeleteShippingAddress() {
    given(shippingAddressRepository.findById(1L)).willReturn(Optional.of(persistedShippingAddress));

    shippingAddressService.deleteUserShippingAddress(1L, user);

    then(shippingAddressRepository).should().delete(persistedShippingAddress);
  }

  @Test
  void
      testDeleteUserShippingAddresss_WhenNonExistingShippingAddressIdIsProvided_ShouldThrowShippingAddressNotFoundException() {
    given(shippingAddressRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> shippingAddressService.deleteUserShippingAddress(99L, user))
        .isInstanceOf(ShippingAddressNotFoundException.class);
  }

  @Test
  void
      testDeleteUserShippingAddresss_WhenShippingAddressIsNotOwnedByCurrentUser_ShouldThrowShippingAddressOwnershipException() {
    User otherUser = new User("email@email2.com", "username2", "password2", "123456789");
    otherUser.setId(2L);
    given(shippingAddressRepository.findById(1L)).willReturn(Optional.of(persistedShippingAddress));

    assertThatThrownBy(() -> shippingAddressService.deleteUserShippingAddress(1L, otherUser))
        .isInstanceOf(ShippingAddressOwnershipException.class);
  }
}
