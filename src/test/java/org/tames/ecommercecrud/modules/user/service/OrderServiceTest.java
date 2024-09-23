package org.tames.ecommercecrud.modules.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.paymentmethod.exception.PaymentMethodNotFoundException;
import org.tames.ecommercecrud.modules.paymentmethod.repository.PaymentMethodRepository;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.user.dto.ItemRequestDto;
import org.tames.ecommercecrud.modules.user.dto.ItemResponseDto;
import org.tames.ecommercecrud.modules.user.dto.OrderResponseDto;
import org.tames.ecommercecrud.modules.user.dto.SaveOrderRequestDto;
import org.tames.ecommercecrud.modules.user.entity.Item;
import org.tames.ecommercecrud.modules.user.entity.Order;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;
import org.tames.ecommercecrud.modules.user.exception.OrderNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.OrderOwnershipException;
import org.tames.ecommercecrud.modules.user.mapper.ItemMapper;
import org.tames.ecommercecrud.modules.user.mapper.OrderMapper;
import org.tames.ecommercecrud.modules.user.repository.OrderRepository;
import org.tames.ecommercecrud.modules.user.specification.OrderSpecs.OrderFilter;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
  @Mock OrderRepository orderRepository;
  @Mock PaymentMethodRepository paymentMethodRepository;
  @Mock ProductRepository productRepository;
  @Mock OrderMapper orderMapper;
  @Mock ItemMapper itemMapper;

  @InjectMocks OrderService orderService;

  User user;
  PaymentMethod paymentMethod;
  Product product;
  Item item;
  ItemRequestDto itemRequestDto;
  ItemResponseDto itemResponseDto;

  Order mappedOrder;
  Order persistedOrder;
  SaveOrderRequestDto orderRequestDto;
  OrderResponseDto orderResponseDto;

  @BeforeEach
  void setUp() {
    paymentMethod = new PaymentMethod("Payment method 1", BigDecimal.valueOf(0.2));
    paymentMethod.setId(1L);
    user = new User("email@email.com", "userename", "password", "123456");
    user.setId(1L);
    product = new Product("name", BigDecimal.valueOf(25.00), "description", 10);
    product.setId(1L);
    itemRequestDto = new ItemRequestDto(1L, 10);
    item = new Item(BigDecimal.valueOf(25.00), 10, product);
    itemResponseDto = new ItemResponseDto(BigDecimal.valueOf(25.00), 10, "Product 1");

    orderRequestDto = new SaveOrderRequestDto(1L, List.of(itemRequestDto));
    orderResponseDto =
        new OrderResponseDto(
            1L,
            LocalDate.parse("2024-10-12"),
            OrderStatus.AWAITING_PAYMENT,
            "Payment method 1",
            List.of(itemResponseDto),
            BigDecimal.valueOf(100.0),
            BigDecimal.valueOf(300.0));

    mappedOrder =
        new Order(LocalDate.parse("2024-10-12"), OrderStatus.AWAITING_PAYMENT, paymentMethod, user);

    persistedOrder =
        new Order(LocalDate.parse("2024-10-12"), OrderStatus.AWAITING_PAYMENT, paymentMethod, user);
    persistedOrder.setId(1L);
  }

  @Test
  void testGetUserOrders_WhenPageableParamsAndFilterAreProvided_ShouldReturnCurrentUserOrders() {
    Pageable pageable = PageRequest.of(0, 50);
    OrderFilter orderFilter =
        new OrderFilter(LocalDate.parse("2024-10-12"), OrderStatus.AWAITING_PAYMENT);

    given(orderRepository.findAll(any(Specification.class), eq(pageable)))
        .willReturn(new PageImpl<>(List.of(persistedOrder)));
    given(orderMapper.toDto(persistedOrder)).willReturn(orderResponseDto);

    PagedModel<OrderResponseDto> result = orderService.getUserOrders(pageable, orderFilter, user);

    assertThat(result).isNotNull();
    assertThat(result.getContent())
        .isNotNull()
        .hasSize(1)
        .first()
        .extracting(
            OrderResponseDto::id,
            OrderResponseDto::date,
            OrderResponseDto::paymentMethod,
            OrderResponseDto::status,
            OrderResponseDto::total,
            OrderResponseDto::transactionFee)
        .containsExactly(
            orderResponseDto.id(),
            orderResponseDto.date(),
            orderResponseDto.paymentMethod(),
            orderResponseDto.status(),
            orderResponseDto.total(),
            orderResponseDto.transactionFee());
  }

  @Test
  void testCreateUserOrder_WhenRequestDtoIsProvided_ShouldReturnCreatedUserOrder() {
    given(paymentMethodRepository.findById(1L)).willReturn(Optional.of(paymentMethod));
    given(productRepository.findAllById(List.of(1L))).willReturn(List.of(product));
    given(itemMapper.toEntity(itemRequestDto, product)).willReturn(item);
    given(orderMapper.toEntity(paymentMethod, user, List.of(item))).willReturn(mappedOrder);
    given(orderRepository.save(mappedOrder)).willReturn(persistedOrder);
    given(orderMapper.toDto(persistedOrder)).willReturn(orderResponseDto);

    OrderResponseDto result = orderService.createUserOrder(orderRequestDto, user);

    assertThat(result)
        .isNotNull()
        .extracting(
            OrderResponseDto::id,
            OrderResponseDto::date,
            OrderResponseDto::paymentMethod,
            OrderResponseDto::status,
            OrderResponseDto::total,
            OrderResponseDto::transactionFee)
        .containsExactly(
            orderResponseDto.id(),
            orderResponseDto.date(),
            orderResponseDto.paymentMethod(),
            orderResponseDto.status(),
            orderResponseDto.total(),
            orderResponseDto.transactionFee());
  }

  @Test
  void testCreateUserOrder_WhenPaymentMethodIsNotFound_ShouldThrowPaymentMethodNotFoundException() {
    given(paymentMethodRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.createUserOrder(orderRequestDto, user))
        .isInstanceOf(PaymentMethodNotFoundException.class);
  }

  @Test
  void testGetUserOrder_WhenExistingOrderIdIsProvided_ShouldReturnUserOrder() {
    given(orderRepository.findById(1L)).willReturn(Optional.of(persistedOrder));
    given(orderMapper.toDto(persistedOrder)).willReturn(orderResponseDto);

    OrderResponseDto result = orderService.getUserOrder(1L, user);

    assertThat(result)
        .isNotNull()
        .extracting(
            OrderResponseDto::id,
            OrderResponseDto::date,
            OrderResponseDto::paymentMethod,
            OrderResponseDto::status,
            OrderResponseDto::total,
            OrderResponseDto::transactionFee)
        .containsExactly(
            orderResponseDto.id(),
            orderResponseDto.date(),
            orderResponseDto.paymentMethod(),
            orderResponseDto.status(),
            orderResponseDto.total(),
            orderResponseDto.transactionFee());
  }

  @Test
  void testGetUserOrder_WhenNonExistingOrderIdIsprovided_ShouldThrowOrderNotFoundException() {
    given(orderRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.getUserOrder(99L, user))
        .isInstanceOf(OrderNotFoundException.class);
  }

  @Test
  void testGetUserOrder_WhenOrderIsNotOwnedByCurrentUser_ShoulThrowOrderOwnershipException() {
    User otherUser = new User("email@email2.com", "username2", "password2", "12345");
    otherUser.setId(2L);

    given(orderRepository.findById(1L)).willReturn(Optional.of(persistedOrder));

    assertThatThrownBy(() -> orderService.getUserOrder(1L, otherUser))
        .isInstanceOf(OrderOwnershipException.class);
  }

  @Test
  void testDeleteUserOrder_WhenExistingOrderIdIsProvided_ShouldDeleteUserOrder() {
    given(orderRepository.findById(1L)).willReturn(Optional.of(persistedOrder));

    orderService.deleteUserOrder(1L, user);

    then(orderRepository).should().delete(persistedOrder);
  }

  @Test
  void testDeleteUserOrder_WhenNonExistingOrderIdIsProvided_ShoulThrowOrderNotFoundException() {
    given(orderRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.deleteUserOrder(99L, user))
        .isInstanceOf(OrderNotFoundException.class);
  }

  @Test
  void
      testDeleteUserOrder_WhenOrderIsNotOwnedByTheCurrentUser_ShouldThrowOrderOwnershipException() {
    User otherUser = new User("email@email2.com", "username2", "password2", "12345");
    otherUser.setId(2L);

    given(orderRepository.findById(1L)).willReturn(Optional.of(persistedOrder));

    assertThatThrownBy(() -> orderService.deleteUserOrder(1L, otherUser))
        .isInstanceOf(OrderOwnershipException.class);
  }
}
