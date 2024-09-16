package org.tames.ecommercecrud.modules.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record SaveProductRequestDto(
    @NotEmpty String name,
    @NotNull @Positive BigDecimal price,
    @NotEmpty String description,
    @NotNull @Min(0) Integer stockQuantity,
    @NotNull List<@Min(1) Long> categoryIds) {}
