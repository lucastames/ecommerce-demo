package org.tames.ecommercecrud.modules.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.review.enums.Rating;

@Entity
@Table(name = "review")
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty private String description;

  @NotNull private LocalDate date;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Rating rating;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  public Review() {}

  public Review(String description, LocalDate date, Rating rating, Product product) {
    this.description = description;
    this.date = date;
    this.rating = rating;
    this.product = product;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String descricao) {
    this.description = descricao;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate data) {
    this.date = data;
  }

  public Rating getRating() {
    return rating;
  }

  public void setRating(Rating rating) {
    this.rating = rating;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Review review)) return false;
    return id != null && Objects.equals(id, review.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
