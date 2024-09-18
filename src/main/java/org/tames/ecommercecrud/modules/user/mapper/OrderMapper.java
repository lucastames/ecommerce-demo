package org.tames.ecommercecrud.modules.user.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.user.dto.OrderResponseDto;
import org.tames.ecommercecrud.modules.user.entity.Item;
import org.tames.ecommercecrud.modules.user.entity.Order;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;

@Component
public class OrderMapper {
  private final ItemMapper itemMapper;

  public OrderMapper(ItemMapper itemMapper) {
    this.itemMapper = itemMapper;
  }

  public OrderResponseDto toDto(Order order) {
    BigDecimal totalWithoutTransactionFee =
        order.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal transactionFee =
        totalWithoutTransactionFee.multiply(order.getPaymentMethod().getTransactionFee());

    return new OrderResponseDto(
        order.getId(),
        order.getDate(),
        order.getStatus(),
        order.getPaymentMethod().getName(),
        itemMapper.toDto(order.getItems()),
        transactionFee,
        totalWithoutTransactionFee.add(transactionFee));
  }

  public Order toEntity(PaymentMethod paymentMethod, User user, List<Item> items) {
    Order order = new Order(LocalDate.now(), OrderStatus.AWAITING_PAYMENT, paymentMethod, user);
    items.forEach(order::addItem);

    return order;
  }
}
