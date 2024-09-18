package org.tames.ecommercecrud.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;
import org.tames.ecommercecrud.modules.user.pk.ItemPk;

@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate date;

  @DecimalMin("0.0")
  @DecimalMax("1.0")
  @NotNull
  BigDecimal transactionFee;

  @Enumerated(EnumType.STRING)
  @NotNull
  private OrderStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_method_id")
  private PaymentMethod paymentMethod;

  @OneToMany(mappedBy = "itemPk.order", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Item> items = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  public Order() {}

  public Order(LocalDate date, OrderStatus status, PaymentMethod paymentMethod, User user) {
    this.date = date;
    this.status = status;
    this.paymentMethod = paymentMethod;
    this.transactionFee = paymentMethod.getTransactionFee();
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public BigDecimal getTransactionFee() {
    return transactionFee;
  }

  public void setTransactionFee(BigDecimal transactionFee) {
    this.transactionFee = transactionFee;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public List<Item> getItems() {
    return items;
  }

  public void addItem(Item item) {
    items.add(item);
    item.getItemPk().setItemNumber(items.size());
    item.getItemPk().setOrder(this);
  }

  public void removeItem(Item item) {
    items.remove(item);
    for (int i = item.getItemPk().getItemNumber() - 1; i < items.size(); i++) {
      ItemPk currentItemPk = items.get(i).getItemPk();
      currentItemPk.setItemNumber(currentItemPk.getItemNumber() - 1);
    }
    item.getItemPk().setOrder(null);
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
