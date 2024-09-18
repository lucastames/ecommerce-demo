package org.tames.ecommercecrud.modules.user.dto;

public record ShippingAddressResponseDto(
    Long id, String address, String additionalInfo, String city, String postalCode) {}
