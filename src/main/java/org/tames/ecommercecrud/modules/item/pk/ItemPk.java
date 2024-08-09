package org.tames.ecommercecrud.modules.item.pk;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import org.tames.ecommercecrud.modules.order.entity.Order;

@Embeddable
public class ItemPk implements Serializable {
  @NotNull private Integer itemNumber;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id")
  private Order order;

  public ItemPk() {}

  public ItemPk(Integer itemNumber, Order order) {
    this.itemNumber = itemNumber;
    this.order = order;
  }

  public Integer getItemNumber() {
    return itemNumber;
  }

  public void setItemNumber(Integer itemNumber) {
    this.itemNumber = itemNumber;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemPk itemPk)) return false;
    return Objects.equals(itemNumber, itemPk.itemNumber) && Objects.equals(order, itemPk.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemNumber, order);
  }
}
