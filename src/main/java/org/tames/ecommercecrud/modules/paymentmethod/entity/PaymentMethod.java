package org.tames.ecommercecrud.modules.paymentmethod.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "payment_method")
public class PaymentMethod {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Min(0)
  @Max(1)
  private BigDecimal transactionFee;

  @NotEmpty private String name;

  public PaymentMethod() {}

  public PaymentMethod(String name, BigDecimal transactionFee) {
    this.name = name;
    this.transactionFee = transactionFee;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getTransactionFee() {
    return transactionFee;
  }

  public void setTransactionFee(BigDecimal transactionFee) {
    this.transactionFee = transactionFee;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PaymentMethod that)) return false;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
