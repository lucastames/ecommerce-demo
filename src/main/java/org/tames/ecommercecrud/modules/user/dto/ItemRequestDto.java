package org.tames.ecommercecrud.modules.user.dto;

import jakarta.validation.constraints.Positive;

public record ItemRequestDto(@Positive Long productId, @Positive Integer quantity) {}
