package org.tames.ecommercecrud.modules.paymentmethod.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.paymentmethod.exception.PaymentMethodNotFoundException;
import org.tames.ecommercecrud.modules.paymentmethod.mapper.PaymentMethodMapper;
import org.tames.ecommercecrud.modules.paymentmethod.repository.PaymentMethodRepository;
import org.tames.ecommercecrud.modules.paymentmethod.specification.PaymentMethodSpecification;

@Service
public class PaymentMethodService {
  private final PaymentMethodRepository paymentMethodRepository;
  private final PaymentMethodMapper paymentMethodMapper;

  public PaymentMethodService(
      PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
    this.paymentMethodRepository = paymentMethodRepository;
    this.paymentMethodMapper = paymentMethodMapper;
  }

  public PagedModel<PaymentMethodResponseDto> getPaymentMethods(
      Pageable pageable, PaymentMethodSpecification specification) {
    Page<PaymentMethodResponseDto> paymentMethodPage =
        paymentMethodRepository.findAll(specification, pageable).map(paymentMethodMapper::toDto);

    return new PagedModel<>(paymentMethodPage);
  }

  public PaymentMethodResponseDto getPaymentMethod(Long paymentMethodId) {
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new PaymentMethodNotFoundException(paymentMethodId));

    return paymentMethodMapper.toDto(paymentMethod);
  }

  @Transactional
  public PaymentMethodResponseDto createPaymentMethod(
      SavePaymentMethodRequestDto savePaymentMethodRequestDto) {
    PaymentMethod createdPaymentMethod =
        paymentMethodRepository.save(paymentMethodMapper.toEntity(savePaymentMethodRequestDto));

    return paymentMethodMapper.toDto(createdPaymentMethod);
  }

  @Transactional
  public PaymentMethodResponseDto updatePaymentMethod(
      Long paymentMethodId, SavePaymentMethodRequestDto savePaymentMethodRequestDto) {
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new PaymentMethodNotFoundException(paymentMethodId));

    paymentMethodMapper.updateFromDto(paymentMethod, savePaymentMethodRequestDto);

    return paymentMethodMapper.toDto(paymentMethodRepository.save(paymentMethod));
  }

  @Transactional
  public void deletePaymentMethod(Long paymentMethodId) {
    PaymentMethod paymentMethod =
        paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new PaymentMethodNotFoundException(paymentMethodId));

    paymentMethodRepository.delete(paymentMethod);
  }
}
