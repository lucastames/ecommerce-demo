package org.tames.ecommercecrud.modules.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record SaveShippingAddressRequestDto(
    @NotEmpty @Size(max = 255) String address,
    @NotEmpty @Size(max = 255) String additionalInfo,
    @NotEmpty @Size(max = 255) String city,
    @NotEmpty @Size(max = 8) String postalCode) {}
