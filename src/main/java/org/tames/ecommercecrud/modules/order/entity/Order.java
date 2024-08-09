package org.tames.ecommercecrud.modules.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.tames.ecommercecrud.modules.item.entity.Item;
import org.tames.ecommercecrud.modules.order.enums.OrderStatus;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;

@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private LocalDate date;

  @Enumerated(EnumType.STRING)
  @NotNull
  private OrderStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_method_id")
  private PaymentMethod paymentMethod;

  @OneToMany(mappedBy = "itemPk.order")
  private final List<Item> items = new ArrayList<>();

  public Order() {}

  public Order(LocalDate date, OrderStatus status, PaymentMethod paymentMethod) {
    this.date = date;
    this.status = status;
    this.paymentMethod = paymentMethod;
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
    item.getItemPk().setOrder(this);
  }

  public void removeItem(Item item) {
    items.remove(item);
    item.getItemPk().setOrder(null);
  }
}
