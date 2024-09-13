package org.tames.ecommercecrud.modules.category.dto;

import jakarta.validation.constraints.NotEmpty;

public record SaveCategoryRequestDto(@NotEmpty String name) {}
