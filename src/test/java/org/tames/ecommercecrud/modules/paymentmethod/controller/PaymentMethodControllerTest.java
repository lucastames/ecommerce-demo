package org.tames.ecommercecrud.modules.paymentmethod.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.test.web.servlet.MockMvc;
import org.tames.ecommercecrud.annotations.WithMockAdmin;
import org.tames.ecommercecrud.annotations.WithMockCustomer;
import org.tames.ecommercecrud.config.ControllerTestConfig;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.modules.paymentmethod.exception.PaymentMethodNotFoundException;
import org.tames.ecommercecrud.modules.paymentmethod.service.PaymentMethodService;
import org.tames.ecommercecrud.modules.paymentmethod.specification.PaymentMethodSpecs.PaymentMethodFilter;

@WebMvcTest(PaymentMethodController.class)
@Import(ControllerTestConfig.class)
@WithMockAdmin
public class PaymentMethodControllerTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean PaymentMethodService paymentMethodService;

  private SavePaymentMethodRequestDto paymentMethodRequestDto;
  private SavePaymentMethodRequestDto paymentMethodInvalidRequestDto;
  private PaymentMethodResponseDto paymentMethodResponseDto;

  @BeforeEach
  void setUp() {
    paymentMethodRequestDto = new SavePaymentMethodRequestDto("Cash", new BigDecimal("0.5"));
    paymentMethodInvalidRequestDto = new SavePaymentMethodRequestDto("", new BigDecimal("-0.5"));
    paymentMethodResponseDto = new PaymentMethodResponseDto(1L, "Cash", new BigDecimal("0.5"));
  }

  @Test
  void
      testCreatePaymentMethod_WhenValidDtoIsProvided_ShouldReturnPaymentMethodResponseDtoAndCreatedStatus()
          throws Exception {

    given(paymentMethodService.createPaymentMethod(paymentMethodRequestDto))
        .willReturn(paymentMethodResponseDto);

    mockMvc
        .perform(
            post("/payment-methods")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodRequestDto)))
        .andExpectAll(
            status().isCreated(),
            header().exists("Location"),
            jsonPath("$.id").value(paymentMethodResponseDto.id()),
            jsonPath("$.name").value(paymentMethodResponseDto.name()),
            jsonPath("$.transactionFee").value(paymentMethodResponseDto.transactionFee()));
  }

  @Test
  void testCreatePaymentMethod_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    mockMvc
        .perform(
            post("/payment-methods")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodInvalidRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockCustomer
  void testCreatePaymentMethod_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            post("/payment-methods")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void
      testGetPaymentMethods_WhenProvidingAllParameters_ShouldReturnPagedModelWithPaymentMethodResponseDtoAndOkStatus()
          throws Exception {
    PaymentMethodFilter filter = new PaymentMethodFilter(null, new BigDecimal("0.25"));
    PageRequest pageRequest = PageRequest.of(0, 30, Sort.by("name", "transactionFee"));
    List<PaymentMethodResponseDto> responseDtoList =
        List.of(
            new PaymentMethodResponseDto(1L, "Cash", new BigDecimal("0.25")),
            new PaymentMethodResponseDto(3L, "Check", new BigDecimal("0.25")),
            new PaymentMethodResponseDto(2L, "Credit card", new BigDecimal("0.25")));
    PagedModel<PaymentMethodResponseDto> pagedModel =
        new PagedModel<>(new PageImpl<>(responseDtoList, pageRequest, responseDtoList.size()));

    given(paymentMethodService.getPaymentMethods(pageRequest, filter)).willReturn(pagedModel);

    mockMvc
        .perform(
            get("/payment-methods")
                .queryParam("page", "0")
                .queryParam("size", "30")
                .queryParam("sort", "name")
                .queryParam("sort", "transactionFee")
                .queryParam("transactionFee", "0.25"))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.content.size()", is(3)),
            jsonPath("$.page.size", is(30)),
            jsonPath("$.page.number", is(0)));
  }

  @Test
  void
      testGetPaymentMethod_WhenExistingIdIsProvided_ShouldReturnPaymentMethodResponseDtoAndOkStatus()
          throws Exception {
    given(paymentMethodService.getPaymentMethod(1L)).willReturn(paymentMethodResponseDto);

    mockMvc
        .perform(get("/payment-methods/{paymentMethodId}", 1L).accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(paymentMethodResponseDto.id()),
            jsonPath("$.name").value(paymentMethodResponseDto.name()),
            jsonPath("$.transactionFee").value(paymentMethodResponseDto.transactionFee()));
  }

  @Test
  void testGetPaymentMethod_WhenNonExistingIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(paymentMethodService.getPaymentMethod(2L))
        .willThrow(new PaymentMethodNotFoundException(2L));

    mockMvc
        .perform(get("/payment-methods/{paymentMethodId}", 2L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void
      testUpdatePaymentMethod_WhenValidDtoIsProvided_ShouldReturnPaymentMethodResponseDtoAndOkStatus()
          throws Exception {
    given(paymentMethodService.updatePaymentMethod(1L, paymentMethodRequestDto))
        .willReturn(paymentMethodResponseDto);

    mockMvc
        .perform(
            put("/payment-methods/{paymentMethodId}", 1L)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodRequestDto)))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(paymentMethodResponseDto.id()),
            jsonPath("$.name").value(paymentMethodResponseDto.name()),
            jsonPath("$.transactionFee").value(paymentMethodResponseDto.transactionFee()));
  }

  @Test
  void testUpdatePaymentMethod_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus()
      throws Exception {
    mockMvc
        .perform(
            put("/payment-methods/{paymentMethodId}", 1L)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodInvalidRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdatePaymentMethod_WhenNonExistingIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(paymentMethodService.updatePaymentMethod(eq(2L), any(SavePaymentMethodRequestDto.class)))
        .willThrow(new PaymentMethodNotFoundException(2L));

    mockMvc
        .perform(
            put("/payment-methods/{paymentMethodId}", 2L)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodRequestDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockCustomer
  void testUpdatedPaymentMethod_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            put("/payment-methods/{paymentMethodId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentMethodRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeletePaymentMethod_WhenExistingIdIsProvided_ShouldReturnNoContentStatus()
      throws Exception {
    mockMvc
        .perform(delete("/payment-methods/{paymentMethodId}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeletePaymentMethod_WhenNonExistingIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    willThrow(new PaymentMethodNotFoundException(2L))
        .given(paymentMethodService)
        .deletePaymentMethod(2L);

    mockMvc
        .perform(delete("/payment-methods/{paymentMethodId}", 2L))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockCustomer
  void testDeletePaymentMethod_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(delete("/payment-methods/{paymentMethodId}", 1L).accept(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
