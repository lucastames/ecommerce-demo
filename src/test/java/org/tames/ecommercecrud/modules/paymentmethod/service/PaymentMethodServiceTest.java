package org.tames.ecommercecrud.modules.paymentmethod.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.paymentmethod.exception.PaymentMethodNotFoundException;
import org.tames.ecommercecrud.modules.paymentmethod.mapper.PaymentMethodMapper;
import org.tames.ecommercecrud.modules.paymentmethod.repository.PaymentMethodRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentMethodService Tests")
public class PaymentMethodServiceTest {
  @InjectMocks PaymentMethodService paymentMethodService;
  @Mock PaymentMethodRepository paymentMethodRepository;
  @Mock PaymentMethodMapper paymentMethodMapper;

  SavePaymentMethodRequestDto requestDto;
  PaymentMethodResponseDto responseDto;
  PaymentMethod mappedEntity;
  PaymentMethod savedEntity;

  @BeforeEach
  void setUp() {
    requestDto = new SavePaymentMethodRequestDto("Credit card", new BigDecimal("0.20"));
    responseDto = new PaymentMethodResponseDto(1L, "Credit card", new BigDecimal("0.20"));
    mappedEntity = new PaymentMethod("Credit card", new BigDecimal("0.20"));
    savedEntity = new PaymentMethod("Credit card", new BigDecimal("0.20"));
    savedEntity.setId(1L);
  }

  @Test
  void
      testGetPaymentMethod_WhenExistingPaymentMethodIdIsProvied_ShouldReturnPaymentMethodResponseDto() {
    given(paymentMethodRepository.findById(1L)).willReturn(Optional.of(savedEntity));
    given(paymentMethodMapper.toDto(savedEntity)).willReturn(responseDto);

    PaymentMethodResponseDto result = paymentMethodService.getPaymentMethod(1L);

    assertThat(result)
        .isNotNull()
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(responseDto.id(), responseDto.name(), responseDto.transactionFee());
  }

  @Test
  void
      testGetPaymentMethod_WhenNonExistingPaymentMethodIdIsProvided_ShouldThrowPaymentNotFoundException() {
    given(paymentMethodRepository.findById(anyLong())).willReturn(Optional.empty());

    assertThatThrownBy(() -> paymentMethodService.getPaymentMethod(2L))
        .isInstanceOf(PaymentMethodNotFoundException.class);
  }

  @Test
  void
      testUpdatePaymentMethod_WhenExistingPaymentMethodIdAndValidDtoIsProvided_ShouldReturnPaymentMethodResponseDto() {
    PaymentMethod updatedEntity = new PaymentMethod("Cash", new BigDecimal("0.0"));
    updatedEntity.setId(1L);
    SavePaymentMethodRequestDto updateRequestDto =
        new SavePaymentMethodRequestDto("Cash", new BigDecimal("0.0"));
    PaymentMethodResponseDto updatedResponseDto =
        new PaymentMethodResponseDto(1L, "Cash", new BigDecimal("0.0"));

    given(paymentMethodRepository.findById(1L)).willReturn(Optional.of(savedEntity));
    given(paymentMethodRepository.save(savedEntity)).willReturn(updatedEntity);
    given(paymentMethodMapper.toDto(updatedEntity)).willReturn(updatedResponseDto);

    PaymentMethodResponseDto result =
        paymentMethodService.updatePaymentMethod(1L, updateRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(
            updatedResponseDto.id(),
            updatedResponseDto.name(),
            updatedResponseDto.transactionFee());
    then(paymentMethodMapper).should().updateFromDto(savedEntity, updateRequestDto);
  }

  @Test
  void
      testUpdatePaymentMethod_WhenNonExistingPaymentMethodIdIsProvided_ShouldThrowPaymentMethodNotFoundException() {
    given(paymentMethodRepository.findById(anyLong())).willReturn(Optional.empty());

    assertThatThrownBy(() -> paymentMethodService.updatePaymentMethod(2L, requestDto))
        .isInstanceOf(PaymentMethodNotFoundException.class);
  }

  @Test
  void testCreatePaymentMethod_WhenValidDtoIsProvided_ShouldReturnPaymentMethodResponseDto() {
    given(paymentMethodMapper.toEntity(requestDto)).willReturn(mappedEntity);
    given(paymentMethodRepository.save(mappedEntity)).willReturn(savedEntity);
    given(paymentMethodMapper.toDto(savedEntity)).willReturn(responseDto);

    PaymentMethodResponseDto result = paymentMethodService.createPaymentMethod(requestDto);

    assertThat(result)
        .isNotNull()
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(responseDto.id(), responseDto.name(), responseDto.transactionFee());
  }

  @Test
  void testDeletePaymentMethod_WhenExistingPaymentMethodIdIsProvided_ShouldInvokeDelete() {
    given(paymentMethodRepository.findById(1L)).willReturn(Optional.of(savedEntity));

    paymentMethodService.deletePaymentMethod(1L);

    then(paymentMethodRepository).should().delete(savedEntity);
  }

  @Test
  void
      testDeletePaymentMethod_WhenNonExistingPaymentMethodIdIsProvided_ShouldThrowPaymentMethodNotFoundException() {
    given(paymentMethodRepository.findById(anyLong())).willReturn(Optional.empty());

    assertThatThrownBy(() -> paymentMethodService.deletePaymentMethod(2L))
        .isInstanceOf(PaymentMethodNotFoundException.class);
  }
}
