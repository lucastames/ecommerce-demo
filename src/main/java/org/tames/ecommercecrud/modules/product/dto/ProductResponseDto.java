package org.tames.ecommercecrud.modules.product.dto;

import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.entity.Category;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDto(
    Long id,
    String name,
    BigDecimal price,
    String description,
    Integer stockQuantity,
    List<CategoryResponseDto> categories) {}
