package org.tames.ecommercecrud.modules.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.tames.ecommercecrud.modules.item.pk.ItemPk;
import org.tames.ecommercecrud.modules.product.entity.Product;

@Entity
@Table(name = "item")
public class Item {
  @EmbeddedId private ItemPk itemPk;

  @NotNull private BigDecimal unitPrice;

  @NotNull private Integer quantity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  public Item() {}

  public Item(ItemPk itemPk, BigDecimal unitPrice, Integer quantity, Product product) {
    this.itemPk = itemPk;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.product = product;
  }

  public ItemPk getItemPk() {
    return itemPk;
  }

  public void setItemPk(ItemPk itemPk) {
    this.itemPk = itemPk;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
