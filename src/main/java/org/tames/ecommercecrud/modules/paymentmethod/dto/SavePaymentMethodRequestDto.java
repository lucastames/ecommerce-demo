package org.tames.ecommercecrud.modules.paymentmethod.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SavePaymentMethodRequestDto(
    @NotEmpty String name,
    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull BigDecimal transactionFee) {}
