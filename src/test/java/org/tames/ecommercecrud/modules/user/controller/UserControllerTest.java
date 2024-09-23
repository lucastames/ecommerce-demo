package org.tames.ecommercecrud.modules.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tames.ecommercecrud.annotations.WithMockCustomer;
import org.tames.ecommercecrud.config.ControllerTestConfig;
import org.tames.ecommercecrud.modules.user.dto.*;
import org.tames.ecommercecrud.modules.user.entity.User;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;
import org.tames.ecommercecrud.modules.user.exception.OrderNotFoundException;
import org.tames.ecommercecrud.modules.user.exception.ShippingAddressNotFoundException;
import org.tames.ecommercecrud.modules.user.service.OrderService;
import org.tames.ecommercecrud.modules.user.service.ShippingAddressService;
import org.tames.ecommercecrud.modules.user.service.UserService;
import org.tames.ecommercecrud.modules.user.specification.OrderSpecs.OrderFilter;

@WebMvcTest(UserController.class)
@Import(ControllerTestConfig.class)
@WithMockCustomer
public class UserControllerTest {
  @MockBean UserService userService;
  @MockBean ShippingAddressService shippingAddressService;
  @MockBean OrderService orderService;

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private ItemRequestDto itemRequestDto;
  private ItemResponseDto itemResponseDto;

  private SaveOrderRequestDto orderRequestDto;
  private SaveOrderRequestDto invalidOrderRequestDto;
  private OrderResponseDto orderResponseDto;

  private SaveShippingAddressRequestDto shippingAddressRequestDto;
  private SaveShippingAddressRequestDto invalidShippingAddressRequestDto;
  private ShippingAddressResponseDto shippingAddressResponseDto;

  @BeforeEach
  void setUp() {
    itemRequestDto = new ItemRequestDto(1L, 10);
    itemResponseDto = new ItemResponseDto(BigDecimal.valueOf(100.00), 10, "Product 1");

    orderRequestDto = new SaveOrderRequestDto(1L, List.of(itemRequestDto));
    invalidOrderRequestDto = new SaveOrderRequestDto(1L, null);
    orderResponseDto =
        new OrderResponseDto(
            1L,
            LocalDate.parse("2024-10-10"),
            OrderStatus.AWAITING_PAYMENT,
            "Credit card",
            List.of(itemResponseDto),
            BigDecimal.valueOf(10.00),
            BigDecimal.valueOf(1010.00));

    shippingAddressRequestDto =
        new SaveShippingAddressRequestDto(
            "Some street", "Some additional info", "Some city", "88070111");
    invalidShippingAddressRequestDto = new SaveShippingAddressRequestDto("", "", "", "");
    shippingAddressResponseDto =
        new ShippingAddressResponseDto(
            1L, "Some street", "Some additional info", "Some city", "88070111");
  }

  @Test
  void testGetUser_WhenUserIsAuthenticated_ShouldReturnUserAndOkStatus() throws Exception {
    UserResponseDto userResponseDto =
        new UserResponseDto(1L, "customer@email.com", "customer", "5548991044");
    given(userService.getUser(anyLong())).willReturn(userResponseDto);

    mockMvc
        .perform(get("/users/me").accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(userResponseDto.id()),
            jsonPath("$.email").value(userResponseDto.email()),
            jsonPath("$.username").value(userResponseDto.username()),
            jsonPath("$.phoneNumber").value(userResponseDto.phoneNumber()));
  }

  @Test
  void testGetUserOrders_WhenQueryParamsAreProvided_ShouldReturnPagedModelOfOrdersAndOkStatus()
      throws Exception {
    Pageable pageable = PageRequest.of(0, 50, Sort.by("date"));
    OrderFilter orderFilter =
        new OrderFilter(LocalDate.parse("2024-10-10"), OrderStatus.AWAITING_PAYMENT);
    List<OrderResponseDto> orders = List.of(orderResponseDto);
    PagedModel<OrderResponseDto> ordersPage =
        new PagedModel<>(new PageImpl<>(orders, pageable, orders.size()));

    given(orderService.getUserOrders(eq(pageable), eq(orderFilter), any(User.class)))
        .willReturn(ordersPage);

    mockMvc
        .perform(
            get("/users/me/orders")
                .queryParam("page", "0")
                .queryParam("size", "50")
                .queryParam("sort", "date,asc")
                .queryParam("date", "2024-10-10")
                .queryParam("status", "AWAITING_PAYMENT")
                .accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.content.size()").value(1),
            jsonPath("$.content[0].id").value(orderResponseDto.id()),
            jsonPath("$.content[0].date").value(orderResponseDto.date().toString()),
            jsonPath("$.content[0].paymentMethod").value(orderResponseDto.paymentMethod()),
            jsonPath("$.content[0].status").value(orderResponseDto.status().toString()),
            jsonPath("$.content[0].total").value(orderResponseDto.total()),
            jsonPath("$.content[0].transactionFee").value(orderResponseDto.transactionFee()));
  }

  @Test
  void testCreateUserOrder_WhenValidDtoIsProvided_ShouldReturnCreatedOrderAndCreatedStatus()
      throws Exception {
    given(orderService.createUserOrder(eq(orderRequestDto), any(User.class)))
        .willReturn(orderResponseDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/me/orders")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDto)))
        .andExpectAll(
            status().isCreated(),
            jsonPath("$.id").value(orderResponseDto.id()),
            jsonPath("$.date").value(orderResponseDto.date().toString()),
            jsonPath("$.paymentMethod").value(orderResponseDto.paymentMethod()),
            jsonPath("$.status").value(orderResponseDto.status().toString()),
            jsonPath("$.total").value(orderResponseDto.total()),
            jsonPath("$.transactionFee").value(orderResponseDto.transactionFee()),
            jsonPath("$.items[0].unitPrice").value(orderResponseDto.items().getFirst().unitPrice()),
            jsonPath("$.items[0].quantity").value(orderResponseDto.items().getFirst().quantity()),
            jsonPath("$.items[0].product").value(orderResponseDto.items().getFirst().product()));
  }

  @Test
  void testCreateUserOrder_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/me/orders")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrderRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetUserOrder_WhenExistingOrderIdIsProvided_ShouldReturnOrderAndOkStatus()
      throws Exception {
    given(orderService.getUserOrder(eq(1L), any(User.class))).willReturn(orderResponseDto);

    mockMvc
        .perform(get("/users/me/orders/{orderId}", 1L).accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(orderResponseDto.id()),
            jsonPath("$.date").value(orderResponseDto.date().toString()),
            jsonPath("$.paymentMethod").value(orderResponseDto.paymentMethod()),
            jsonPath("$.status").value(orderResponseDto.status().toString()),
            jsonPath("$.total").value(orderResponseDto.total()),
            jsonPath("$.transactionFee").value(orderResponseDto.transactionFee()),
            jsonPath("$.items[0].unitPrice").value(orderResponseDto.items().getFirst().unitPrice()),
            jsonPath("$.items[0].quantity").value(orderResponseDto.items().getFirst().quantity()),
            jsonPath("$.items[0].product").value(orderResponseDto.items().getFirst().product()));
  }

  @Test
  void testGetUserOrder_WhenNonExistingOrderIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(orderService.getUserOrder(eq(99L), any(User.class)))
        .willThrow(new OrderNotFoundException(99L));

    mockMvc
        .perform(get("/users/me/orders/{orderId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteUserOrder_WhenExistingOrderIdIsProvided_ShouldReturnNoContentStatus()
      throws Exception {
    mockMvc
        .perform(delete("/users/me/orders/{orderId}", 1L).accept(APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(orderService).should().deleteUserOrder(eq(1L), any(User.class));
  }

  @Test
  void testDeleteUserOrder_WhenNonExistingOrderIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    willThrow(new OrderNotFoundException(99L))
        .given(orderService)
        .deleteUserOrder(eq(99L), any(User.class));

    mockMvc
        .perform(delete("/users/me/orders/{orderId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void
      testGetUserShippingAddresses_WhenUserIsAuthenticated_ShouldReturnItsShippingAddressesAndOkStatus()
          throws Exception {
    given(shippingAddressService.getUserShippingAddresses(any(User.class)))
        .willReturn(List.of(shippingAddressResponseDto));

    mockMvc
        .perform(get("/users/me/shipping-addresses").accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.size()").value(1),
            jsonPath("$[0].id").value(shippingAddressResponseDto.id()),
            jsonPath("$[0].city").value(shippingAddressResponseDto.city()),
            jsonPath("$[0].address").value(shippingAddressResponseDto.address()),
            jsonPath("$[0].additionalInfo").value(shippingAddressResponseDto.additionalInfo()),
            jsonPath("$[0].postalCode").value(shippingAddressResponseDto.postalCode()));
  }

  @Test
  void
      testCreateUserShippingAddress_WhenValidDtoIsProvided_ShouldReturnCreatedShippingAddressAndCreatedStatus()
          throws Exception {
    given(
            shippingAddressService.createUserShippingAddress(
                eq(shippingAddressRequestDto), any(User.class)))
        .willReturn(shippingAddressResponseDto);

    mockMvc
        .perform(
            post("/users/me/shipping-addresses")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shippingAddressRequestDto)))
        .andExpectAll(
            status().isCreated(),
            jsonPath("$.id").value(shippingAddressResponseDto.id()),
            jsonPath("$.city").value(shippingAddressResponseDto.city()),
            jsonPath("$.address").value(shippingAddressResponseDto.address()),
            jsonPath("$.additionalInfo").value(shippingAddressResponseDto.additionalInfo()),
            jsonPath("$.postalCode").value(shippingAddressResponseDto.postalCode()));
  }

  @Test
  void testCreateUserShippingAddress_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    mockMvc
        .perform(
            post("/users/me/shipping-addresses")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidShippingAddressRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void
      testUpdateUserShippingAddress_WhenValidDtoIsProvided_ShouldReturnUpdatedShippingAddressAndOkStatus()
          throws Exception {
    given(
            shippingAddressService.updateUserShippingAddress(
                eq(shippingAddressRequestDto), eq(1L), any(User.class)))
        .willReturn(shippingAddressResponseDto);

    mockMvc
        .perform(
            put("/users/me/shipping-addresses/{shippingAddress}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shippingAddressRequestDto)))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(shippingAddressResponseDto.id()),
            jsonPath("$.city").value(shippingAddressResponseDto.city()),
            jsonPath("$.address").value(shippingAddressResponseDto.address()),
            jsonPath("$.additionalInfo").value(shippingAddressResponseDto.additionalInfo()),
            jsonPath("$.postalCode").value(shippingAddressResponseDto.postalCode()));
  }

  @Test
  void testUpdateUserShippingAddress_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus()
      throws Exception {

    mockMvc
        .perform(
            put("/users/me/shipping-addresses/{shippingAddressId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidShippingAddressRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void
      testUpdateUserShippingAddress_WhenNonExistingShippingAddressIdIsProvided_ShouldReturnNotFoundStatus()
          throws Exception {
    given(
            shippingAddressService.updateUserShippingAddress(
                eq(shippingAddressRequestDto), eq(99L), any(User.class)))
        .willThrow(new ShippingAddressNotFoundException(99L));

    mockMvc
        .perform(
            put("/users/me/shipping-addresses/{shippingAddressId}", 99L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shippingAddressRequestDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void
      testDeleteUserShippingAddress_WhenExsitingShippingAddressIdIsProvided_ShouldReturnNoContentStatus()
          throws Exception {
    mockMvc
        .perform(delete("/users/me/shipping-addresses/{shippingAddressId}", 1L))
        .andExpect(status().isNoContent());

    then(shippingAddressService).should().deleteUserShippingAddress(eq(1L), any(User.class));
  }

  @Test
  void
      testDeleteUserShippingAddress_WhenNonExsitingShippingAddressIdIsProvided_ShouldReturnNotFoundStatus()
          throws Exception {
    willThrow(new ShippingAddressNotFoundException(99L))
        .given(shippingAddressService)
        .deleteUserShippingAddress(eq(99L), any(User.class));

    mockMvc
        .perform(delete("/users/me/shipping-addresses/{shippingAddressId}", 99L))
        .andExpect(status().isNotFound());
  }
}
