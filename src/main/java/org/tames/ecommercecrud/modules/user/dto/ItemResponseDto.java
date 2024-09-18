package org.tames.ecommercecrud.modules.user.dto;

import java.math.BigDecimal;

public record ItemResponseDto(BigDecimal unitPrice, Integer quantity, String product) {}
