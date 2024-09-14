package org.tames.ecommercecrud.modules.paymentmethod.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;

public class PaymentMethodSpecs {
  public static class PaymentMethodFilter implements Specification<PaymentMethod> {
    private final String name;
    private final BigDecimal transactionFee;

    public PaymentMethodFilter(String name, BigDecimal transactionFee) {
      this.name = name;
      this.transactionFee = transactionFee;
    }

    @Override
    public Predicate toPredicate(
        Root<PaymentMethod> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
      List<Specification<PaymentMethod>> specifications = new ArrayList<>();

      if (StringUtils.isNotEmpty(name)) {
        specifications.add(byName(name));
      }

      if (Objects.nonNull(transactionFee)) {
        specifications.add(byTransactionFee(transactionFee));
      }

      return Specification.allOf(specifications).toPredicate(root, query, criteriaBuilder);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PaymentMethodFilter that)) return false;
      return Objects.equals(name, that.name) && Objects.equals(transactionFee, that.transactionFee);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, transactionFee);
    }
  }

  public static Specification<PaymentMethod> byName(String name) {
    return (root, cq, cb) -> cb.like(root.get("name"), "%" + name + "%");
  }

  public static Specification<PaymentMethod> byTransactionFee(BigDecimal transactionFee) {
    return (root, cq, cb) -> cb.equal(root.get("transactionFee"), transactionFee);
  }
}
