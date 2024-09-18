package org.tames.ecommercecrud.modules.user.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.user.entity.Order;
import org.tames.ecommercecrud.modules.user.enums.OrderStatus;

public class OrderSpecs {
  public static class OrderFilter implements Specification<Order> {
    private final LocalDate date;
    private final OrderStatus status;

    public OrderFilter(LocalDate date, OrderStatus status) {
      this.date = date;
      this.status = status;
    }

    @Override
    public Predicate toPredicate(
        Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
      List<Specification<Order>> specifications = new ArrayList<>();

      if (date != null) {
        specifications.add(byDate(date));
      }

      if (status != null) {
        specifications.add(byStatus(status));
      }

      return Specification.allOf(specifications).toPredicate(root, query, criteriaBuilder);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof OrderFilter that)) return false;
      return Objects.equals(date, that.date) && status == that.status;
    }

    @Override
    public int hashCode() {
      return Objects.hash(date, status);
    }
  }

  public static Specification<Order> byUserId(Long userId) {
    return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  public static Specification<Order> byDate(LocalDate date) {
    return (root, cq, cb) -> cb.equal(root.get("date"), date);
  }

  public static Specification<Order> byStatus(OrderStatus status) {
    return (root, cq, cb) -> cb.equal(root.get("status"), status);
  }
}
