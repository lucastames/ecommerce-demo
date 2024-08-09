package org.tames.ecommercecrud.modules.paymentmethod.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.modules.paymentmethod.service.PaymentMethodService;
import org.tames.ecommercecrud.modules.paymentmethod.specification.PaymentMethodSpecification;

@RestController
@RequestMapping("payment-methods")
public class PaymentMethodController {
  private final PaymentMethodService paymentMethodService;
  private final Logger log = LogManager.getLogger(PaymentMethodController.class);

  public PaymentMethodController(PaymentMethodService paymentMethodService) {
    this.paymentMethodService = paymentMethodService;
  }

  @GetMapping
  public ResponseEntity<PagedModel<PaymentMethodResponseDto>> getPaymentMethods(
      Pageable pageable, PaymentMethodSpecification specification) {
    return ResponseEntity.ok(paymentMethodService.getPaymentMethods(pageable, specification));
  }

  @GetMapping("/{paymentMethodId}")
  public ResponseEntity<PaymentMethodResponseDto> getPaymentMethod(
      @PathVariable Long paymentMethodId) {
    return ResponseEntity.ok(paymentMethodService.getPaymentMethod(paymentMethodId));
  }

  @PostMapping
  public ResponseEntity<PaymentMethodResponseDto> createPaymentMethod(
      @RequestBody @Valid SavePaymentMethodRequestDto savePaymentMethodRequestDto,
      UriComponentsBuilder ucb) {
    PaymentMethodResponseDto createdPaymentMethod =
        paymentMethodService.createPaymentMethod(savePaymentMethodRequestDto);

    return ResponseEntity.created(
            ucb.path("/payment-methods/{paymentMethodId}").build(createdPaymentMethod.id()))
        .body(createdPaymentMethod);
  }

  @PutMapping("/{paymentMethodId}")
  public ResponseEntity<PaymentMethodResponseDto> updatePaymentMethod(
      @PathVariable Long paymentMethodId,
      @RequestBody @Valid SavePaymentMethodRequestDto savePaymentMethodRequestDto) {
    return ResponseEntity.ok(
        paymentMethodService.updatePaymentMethod(paymentMethodId, savePaymentMethodRequestDto));
  }

  @DeleteMapping("/{paymentMethodId}")
  public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long paymentMethodId) {
    paymentMethodService.deletePaymentMethod(paymentMethodId);
    return ResponseEntity.noContent().build();
  }
}
