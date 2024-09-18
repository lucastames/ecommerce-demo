package org.tames.ecommercecrud.modules.user.mapper;

import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.user.dto.SaveShippingAddressRequestDto;
import org.tames.ecommercecrud.modules.user.dto.ShippingAddressResponseDto;
import org.tames.ecommercecrud.modules.user.entity.ShippingAddress;
import org.tames.ecommercecrud.modules.user.entity.User;

@Component
public class ShippingAddressMapper {
  public ShippingAddress toEntity(
      SaveShippingAddressRequestDto saveShippingAddressRequestDto, User user) {
    return new ShippingAddress(
        saveShippingAddressRequestDto.address(),
        saveShippingAddressRequestDto.additionalInfo(),
        saveShippingAddressRequestDto.city(),
        saveShippingAddressRequestDto.postalCode(),
        user);
  }

  public ShippingAddressResponseDto toDto(ShippingAddress shippingAddress) {
    return new ShippingAddressResponseDto(
        shippingAddress.getId(),
        shippingAddress.getAddress(),
        shippingAddress.getAdditionalInfo(),
        shippingAddress.getCity(),
        shippingAddress.getPostalCode());
  }

  public void updateFromDto(
      ShippingAddress shippingAddress,
      SaveShippingAddressRequestDto saveShippingAddressRequestDto) {
    shippingAddress.setAdditionalInfo(saveShippingAddressRequestDto.additionalInfo());
    shippingAddress.setAddress(saveShippingAddressRequestDto.address());
    shippingAddress.setCity(saveShippingAddressRequestDto.city());
    shippingAddress.setPostalCode(saveShippingAddressRequestDto.postalCode());
  }
}
