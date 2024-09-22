package org.tames.ecommercecrud.modules.paymentmethod.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.tames.ecommercecrud.modules.paymentmethod.dto.PaymentMethodResponseDto;
import org.tames.ecommercecrud.modules.paymentmethod.dto.SavePaymentMethodRequestDto;
import org.tames.ecommercecrud.util.CustomPagedModel;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/it-setup/payment-method-setup.sql")
@Testcontainers
public class PaymentMethodIT {
  @Autowired TestRestTemplate testRestTemplate;

  @Container @ServiceConnection
  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:alpine");

  private SavePaymentMethodRequestDto savePaymentMethodDto;

  @BeforeEach
  void setUp() {
    savePaymentMethodDto = new SavePaymentMethodRequestDto("Debit card", new BigDecimal("0.30"));
  }

  @Test
  void testGetPaymentMethods() {
    ResponseEntity<CustomPagedModel<PaymentMethodResponseDto>> result =
        testRestTemplate.exchange(
            "/payment-methods?page=0&size=30&sort=name,asc",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getContent())
        .isNotNull()
        .hasSize(3)
        .extracting(PaymentMethodResponseDto::name)
        .containsExactly("Cash", "Check", "Credit card");
  }

  @Test
  void testGetPaymentMethod() {
    ResponseEntity<PaymentMethodResponseDto> result =
        testRestTemplate.getForEntity(
            "/payment-methods/{paymentMethodId}", PaymentMethodResponseDto.class, 1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(1L, "Cash", new BigDecimal("0.10"));
  }

  @Test
  void testCreatePaymentMethod() {

    ResponseEntity<PaymentMethodResponseDto> result =
        testRestTemplate.postForEntity(
            "/payment-methods", savePaymentMethodDto, PaymentMethodResponseDto.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getHeaders().getLocation()).isNotNull();
    assertThat(result.getBody())
        .isNotNull()
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(4L, savePaymentMethodDto.name(), savePaymentMethodDto.transactionFee());
  }

  @Test
  void testUpdatePaymentMethod() {
    ResponseEntity<PaymentMethodResponseDto> result =
        testRestTemplate.exchange(
            "/payment-methods/{paymentMethodId}",
            HttpMethod.PUT,
            new HttpEntity<>(savePaymentMethodDto),
            PaymentMethodResponseDto.class,
            1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody())
        .extracting(
            PaymentMethodResponseDto::id,
            PaymentMethodResponseDto::name,
            PaymentMethodResponseDto::transactionFee)
        .containsExactly(1L, savePaymentMethodDto.name(), savePaymentMethodDto.transactionFee());
  }

  @Test
  void testDeletePaymentMethod() {
    ResponseEntity<Void> result =
        testRestTemplate.exchange(
            "/payment-methods/{paymentMethodId}", HttpMethod.DELETE, null, Void.class, 1L);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
