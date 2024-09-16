package org.tames.ecommercecrud.modules.product.dto;

import java.time.LocalDate;
import org.tames.ecommercecrud.modules.product.enums.Rating;

public record ReviewResponseDto(
    Long id, String description, LocalDate date, Rating rating, String author) {}
