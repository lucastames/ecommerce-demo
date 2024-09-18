package org.tames.ecommercecrud.modules.user.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tames.ecommercecrud.modules.user.dto.*;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.service.OrderService;
import org.tames.ecommercecrud.modules.user.service.ShippingAddressService;
import org.tames.ecommercecrud.modules.user.service.UserService;
import org.tames.ecommercecrud.modules.user.specification.OrderSpecs.OrderFilter;

@RequestMapping("users")
@RestController
public class UserController {
  private final ShippingAddressService shippingAddressService;
  private final UserService userService;
  private final OrderService orderService;

  public UserController(
      ShippingAddressService shippingAddressService,
      UserService userService,
      OrderService orderService) {
    this.shippingAddressService = shippingAddressService;
    this.userService = userService;
    this.orderService = orderService;
  }

  @GetMapping("me")
  public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.getUser(user.getId()));
  }

  @GetMapping("me/orders")
  public ResponseEntity<PagedModel<OrderResponseDto>> getUserOrders(
      Pageable pageable, OrderFilter orderFilter, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.getUserOrders(pageable, orderFilter, user));
  }

  @PostMapping("me/orders")
  public ResponseEntity<OrderResponseDto> createUserOrder(
      @RequestBody @Valid SaveOrderRequestDto saveOrderRequestDto,
      @AuthenticationPrincipal User user,
      UriComponentsBuilder ucb) {
    OrderResponseDto order = orderService.createUserOrder(saveOrderRequestDto, user);

    return ResponseEntity.created(ucb.path("/users/me/orders/{orderId}").build(order.id()))
        .body(order);
  }

  @GetMapping("me/orders/{orderId}")
  public ResponseEntity<OrderResponseDto> getUserOrder(
      @PathVariable Long orderId, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.getUserOrder(orderId, user));
  }

  @DeleteMapping("me/orders/{orderId}")
  public ResponseEntity<Void> deleteUserOrder(
      @PathVariable Long orderId, @AuthenticationPrincipal User user) {
    orderService.deleteUserOrder(orderId, user);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("me/shipping-addresses")
  public ResponseEntity<List<ShippingAddressResponseDto>> getUserShippingAddresses(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(shippingAddressService.getUserShippingAddresses(user));
  }

  @PostMapping("me/shipping-addresses")
  public ResponseEntity<ShippingAddressResponseDto> createUserShippingAddress(
      @RequestBody @Valid SaveShippingAddressRequestDto saveShippingAddressRequestDto,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            shippingAddressService.createUserShippingAddress(saveShippingAddressRequestDto, user));
  }

  @PutMapping("me/shipping-addresses/{shippingAddressId}")
  public ResponseEntity<ShippingAddressResponseDto> updateUserShippingAddress(
      @RequestBody @Valid SaveShippingAddressRequestDto saveShippingAddressRequestDto,
      @PathVariable Long shippingAddressId,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(
        shippingAddressService.updateUserShippingAddress(
            saveShippingAddressRequestDto, shippingAddressId, user));
  }

  @DeleteMapping("me/shipping-addresses/{shippingAddressId}")
  public ResponseEntity<Void> deleteUserShippingAddress(
      @PathVariable Long shippingAddressId, @AuthenticationPrincipal User user) {
    shippingAddressService.deleteUserShippingAddress(shippingAddressId, user);

    return ResponseEntity.noContent().build();
  }
}
