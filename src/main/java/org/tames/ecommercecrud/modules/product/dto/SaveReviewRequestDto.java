package org.tames.ecommercecrud.modules.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.tames.ecommercecrud.modules.product.enums.Rating;

public record SaveReviewRequestDto(@NotEmpty String description, @NotNull Rating rating) {}
