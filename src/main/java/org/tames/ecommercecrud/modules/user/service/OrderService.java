package org.tames.ecommercecrud.modules.user.service;

import static org.tames.ecommercecrud.modules.user.specification.OrderSpecs.byUserId;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.paymentmethod.exception.PaymentMethodNotFoundException;
import org.tames.ecommercecrud.modules.paymentmethod.repository.PaymentMethodRepository;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.product.exception.ProductOutOfStockException;
import org.tames.ecommercecrud.modules.product.repository.ProductRepository;
import org.tames.ecommercecrud.modules.user.dto.ItemRequestDto;
import org.tames.ecommercecrud.modules.user.dto.OrderResponseDto;
import org.tames.ecommercecrud.modules.user.dto.SaveOrderRequestDto;
import org.tames.ecommercecrud.modules.user.entity.Item;
import org.tames.ecommercecrud.modules.user.entity.Order;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.exception.OrderNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.OrderOwnershipException;
import org.tames.ecommercecrud.modules.user.mapper.ItemMapper;
import org.tames.ecommercecrud.modules.user.mapper.OrderMapper;
import org.tames.ecommercecrud.modules.user.repository.OrderRepository;
import org.tames.ecommercecrud.modules.user.specification.OrderSpecs.OrderFilter;

@Service
@Transactional(readOnly = true)
public class OrderService {
  private final OrderRepository orderRepository;
  private final PaymentMethodRepository paymentMethodRepository;
  private final OrderMapper orderMapper;
  private final ItemMapper itemMapper;
  private final ProductRepository productRepository;

  public OrderService(
      OrderRepository orderRepository,
      PaymentMethodRepository paymentMethodRepository,
      ProductRepository productRepository,
      OrderMapper orderMapper,
      ItemMapper itemMapper) {
    this.orderRepository = orderRepository;
    this.paymentMethodRepository = paymentMethodRepository;
    this.productRepository = productRepository;
    this.orderMapper = orderMapper;
    this.itemMapper = itemMapper;
  }

  public PagedModel<OrderResponseDto> getUserOrders(
      Pageable pageable, OrderFilter orderFilter, User user) {
    Page<OrderResponseDto> orderPage =
        orderRepository
            .findAll(orderFilter.and(byUserId(user.getId())), pageable)
            .map(orderMapper::toDto);

    return new PagedModel<>(orderPage);
  }

  public OrderResponseDto getUserOrder(Long orderId, User user) {
    Order order = getAndValidateOrder(orderId, user);
    return orderMapper.toDto(order);
  }

  @Transactional
  public OrderResponseDto createUserOrder(SaveOrderRequestDto saveOrderRequestDto, User user) {
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(saveOrderRequestDto.paymentMethodId())
            .orElseThrow(
                () -> new PaymentMethodNotFoundException(saveOrderRequestDto.paymentMethodId()));

    Map<Long, Product> productMap =
        productRepository
            .findAllById(
                saveOrderRequestDto.items().stream().map(ItemRequestDto::productId).toList())
            .stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
    List<Item> items =
        saveOrderRequestDto.items().stream()
            .map(item -> itemMapper.toEntity(item, productMap.get(item.productId())))
            .toList();

    items.forEach(this::validateProductStock);

    Order order = orderMapper.toEntity(paymentMethod, user, items);
    return orderMapper.toDto(orderRepository.save(order));
  }

  @Transactional
  public void deleteUserOrder(Long orderId, User user) {
    Order order = getAndValidateOrder(orderId, user);
    orderRepository.delete(order);
  }

  private Order getAndValidateOrder(Long orderId, User user) {
    Order order =
        orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

    if (!order.getUser().getId().equals(user.getId())) {
      throw new OrderOwnershipException(orderId, user.getUsername());
    }

    return order;
  }

  private void validateProductStock(Item item) {
    Product product = item.getProduct();
    int remainingStock = product.getStockQuantity() - item.getQuantity();

    if (remainingStock < 0) {
      throw new ProductOutOfStockException(product.getId());
    }
    product.setStockQuantity(remainingStock);
  }
}
