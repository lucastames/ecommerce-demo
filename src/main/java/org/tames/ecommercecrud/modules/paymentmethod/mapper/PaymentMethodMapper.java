package org.tames.ecommercecrud.modules.paymentmethod.mapper;

import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;

@Component
public class PaymentMethodMapper {
  public PaymentMethodResponseDto toDto(PaymentMethod paymentMethod) {
    return new PaymentMethodResponseDto(
        paymentMethod.getId(), paymentMethod.getName(), paymentMethod.getTransactionFee());
  }

  public PaymentMethod toEntity(SavePaymentMethodRequestDto savePaymentMethodRequestDto) {
    return new PaymentMethod(
        savePaymentMethodRequestDto.name(), savePaymentMethodRequestDto.transactionFee());
  }

  public void updateFromDto(
      PaymentMethod paymentMethod, SavePaymentMethodRequestDto savePaymentMethodRequestDto) {
    paymentMethod.setName(savePaymentMethodRequestDto.name());
    paymentMethod.setTransactionFee(savePaymentMethodRequestDto.transactionFee());
  }
}
