package org.tames.ecommercecrud.modules.product.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.product.entity.Review;
import org.tames.ecommercecrud.modules.product.enums.Rating;

public class ReviewSpecs {
  public static class ReviewFilter implements Specification<Review> {
    private final String description;
    private final LocalDate date;
    private final Rating rating;

    public ReviewFilter(String description, LocalDate date, Rating rating) {
      this.description = description;
      this.date = date;
      this.rating = rating;
    }

    @Override
    public Predicate toPredicate(
        Root<Review> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
      List<Specification<Review>> specifications = new ArrayList<>();

      if (StringUtils.isNotEmpty(description)) {
        specifications.add(byDescriptionLike(description));
      }

      if (date != null) {
        specifications.add(byDate(date));
      }

      if (rating != null) {
        specifications.add(byRating(rating));
      }

      return Specification.allOf(specifications).toPredicate(root, query, criteriaBuilder);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ReviewFilter that)) return false;
      return Objects.equals(description, that.description)
          && Objects.equals(date, that.date)
          && rating == that.rating;
    }

    @Override
    public int hashCode() {
      return Objects.hash(description, date, rating);
    }
  }

  public static Specification<Review> byRating(Rating rating) {
    return (root, cq, cb) -> cb.equal(root.get("rating"), rating);
  }

  public static Specification<Review> byDescriptionLike(String description) {
    return (root, cq, cb) -> cb.like(root.get("description"), "%" + description + "%");
  }

  public static Specification<Review> byDate(LocalDate date) {
    return (root, cq, cb) -> cb.equal(root.get("date"), date);
  }

  public static Specification<Review> byProductId(Long productId) {
    return (root, cq, cb) -> cb.equal(root.get("product").get("id"), productId);
  }
}
