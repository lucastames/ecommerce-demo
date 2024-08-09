package org.tames.ecommercecrud.modules.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record SaveProductRequestDto(
    String name,
    BigDecimal price,
    String description,
    Integer stockQuantity,
    List<Long> categoryIds) {}
