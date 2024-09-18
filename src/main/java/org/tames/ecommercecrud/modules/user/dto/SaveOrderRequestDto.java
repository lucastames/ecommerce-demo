package org.tames.ecommercecrud.modules.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record SaveOrderRequestDto(
    @Positive Long paymentMethodId, @NotEmpty List<@Valid ItemRequestDto> items) {}
