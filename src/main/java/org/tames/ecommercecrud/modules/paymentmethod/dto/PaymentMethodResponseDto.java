package org.tames.ecommercecrud.modules.paymentmethod.dto;

import java.math.BigDecimal;

public record PaymentMethodResponseDto(Long id, String name, BigDecimal transactionFee) {}
