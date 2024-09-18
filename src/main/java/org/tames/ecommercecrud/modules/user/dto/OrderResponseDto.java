package org.tames.ecommercecrud.modules.user.dto;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;

public record OrderResponseDto(
    Long id,
    LocalDate date,
    OrderStatus status,
    String paymentMethod,
    List<ItemResponseDto> items,
    BigDecimal transactionFee,
    BigDecimal total) {}
