package org.tames.ecommercecrud.modules.product.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.product.entity.Product;

public class ProductSpecs {
  public static class ProductFilter implements Specification<Product> {
    private final String name;
    private final BigDecimal price;
    private final String description;
    private final Integer stockQuantity;

    public ProductFilter(String name, BigDecimal price, String description, Integer stockQuantity) {
      this.name = name;
      this.price = price;
      this.description = description;
      this.stockQuantity = stockQuantity;
    }

    @Override
    public Predicate toPredicate(
        Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

      List<Specification<Product>> specifications = new ArrayList<>();

      if (StringUtils.isNotEmpty(name)) {
        specifications.add(byName(name));
      }

      if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
        specifications.add(byPrice(price));
      }

      if (StringUtils.isNotEmpty(description)) {
        specifications.add(byDescription(description));
      }

      if (stockQuantity != null && stockQuantity >= 0) {
        specifications.add(byStockQuantity(stockQuantity));
      }

      return Specification.allOf(specifications).toPredicate(root, query, criteriaBuilder);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ProductFilter that)) return false;
      return Objects.equals(name, that.name)
          && Objects.equals(price, that.price)
          && Objects.equals(description, that.description)
          && Objects.equals(stockQuantity, that.stockQuantity);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, price, description, stockQuantity);
    }
  }

  public static Specification<Product> byName(String name) {
    return (root, cq, cb) -> cb.like(root.get("name"), "%" + name + "%");
  }

  public static Specification<Product> byPrice(BigDecimal price) {
    return (root, cq, cb) -> cb.equal(root.get("price"), price);
  }

  public static Specification<Product> byDescription(String description) {
    return (root, cq, cb) -> cb.like(root.get("description"), "%" + description + "%");
  }

  public static Specification<Product> byStockQuantity(Integer stockQuantity) {
    return (root, cq, cb) -> cb.equal(root.get("stockQuantity"), stockQuantity);
  }
}
