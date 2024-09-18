package org.tames.ecommercecrud.modules.user.service;

import static org.tames.ecommercecrud.modules.user.specification.ShippingAddressSpecs.byUserId;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.user.dto.SaveShippingAddressRequestDto;
import org.tames.ecommercecrud.modules.user.dto.ShippingAddressResponseDto;
import org.tames.ecommercecrud.modules.user.entity.ShippingAddress;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.exception.MaxShippingAddressesException;
import org.tames.ecommercecrud.modules.user.exception.ShippingAddressNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.ShippingAddressOwnershipException;
import org.tames.ecommercecrud.modules.user.mapper.ShippingAddressMapper;
import org.tames.ecommercecrud.modules.user.repository.ShippingAddressRepository;

@Service
@Transactional(readOnly = true)
public class ShippingAddressService {
  private static final int MAX_SHIPPING_ADDRESSES_PER_USER = 5;
  private final ShippingAddressRepository shippingAddressRepository;
  private final ShippingAddressMapper shippingAddressMapper;

  public ShippingAddressService(
      ShippingAddressRepository shippingAddressRepository,
      ShippingAddressMapper shippingAddressMapper) {
    this.shippingAddressRepository = shippingAddressRepository;
    this.shippingAddressMapper = shippingAddressMapper;
  }

  public List<ShippingAddressResponseDto> getUserShippingAddresses(User user) {
    return shippingAddressRepository.findAll(byUserId(user.getId())).stream()
        .map(shippingAddressMapper::toDto)
        .toList();
  }

  @Transactional
  public ShippingAddressResponseDto createUserShippingAddress(
      SaveShippingAddressRequestDto saveShippingAddressRequestDto, User user) {
    if (shippingAddressRepository.count(byUserId(user.getId()))
        >= MAX_SHIPPING_ADDRESSES_PER_USER) {
      throw new MaxShippingAddressesException(MAX_SHIPPING_ADDRESSES_PER_USER, user.getUsername());
    }

    ShippingAddress shippingAddress =
        shippingAddressMapper.toEntity(saveShippingAddressRequestDto, user);
    return shippingAddressMapper.toDto(shippingAddressRepository.save(shippingAddress));
  }

  @Transactional
  public ShippingAddressResponseDto updateUserShippingAddress(
      SaveShippingAddressRequestDto saveShippingAddressRequestDto,
      Long shippingAddressId,
      User user) {
    ShippingAddress shippingAddress = getAndValidateShippingAddress(shippingAddressId, user);
    shippingAddressMapper.updateFromDto(shippingAddress, saveShippingAddressRequestDto);

    return shippingAddressMapper.toDto(shippingAddressRepository.save(shippingAddress));
  }

  @Transactional
  public void deleteUserShippingAddress(Long shippingAddressId, User user) {
    ShippingAddress shippingAddress = getAndValidateShippingAddress(shippingAddressId, user);
    shippingAddressRepository.delete(shippingAddress);
  }

  private ShippingAddress getAndValidateShippingAddress(Long shippingAddressId, User user) {
    ShippingAddress shippingAddress =
        shippingAddressRepository
            .findById(shippingAddressId)
            .orElseThrow(() -> new ShippingAddressNotFoundException(shippingAddressId));

    if (!shippingAddress.getUser().getId().equals(user.getId())) {
      throw new ShippingAddressOwnershipException(shippingAddressId, user.getUsername());
    }

    return shippingAddress;
  }
}
