package org.tames.ecommercecrud.modules.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.*;
import org.tames.ecommercecrud.modules.category.entity.Category;

@Entity
@Table(name = "product")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty private String name;

  @NotNull @Positive private BigDecimal price;

  @NotEmpty private String description;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  @ManyToMany
  @JoinTable(
      name = "product_category",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Review> reviews = new ArrayList<>();

  public Product() {}

  public Product(String name, BigDecimal price, String description, Integer stockQuantity) {
    this.price = price;
    this.name = name;
    this.description = description;
    this.stockQuantity = stockQuantity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(Integer stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public void addCategory(Category category) {
    categories.add(category);
  }

  public void removeCategory(Category category) {
    categories.remove(category);
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void addReview(Review review) {
    reviews.add(review);
    review.setProduct(this);
  }

  public void removeReview(Review review) {
    reviews.add(review);
    review.setProduct(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product product)) return false;
    return id != null && Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
