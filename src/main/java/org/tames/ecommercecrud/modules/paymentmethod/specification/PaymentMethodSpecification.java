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

public class PaymentMethodSpecification implements Specification<PaymentMethod> {
  private String name;
  private BigDecimal transactionFee;

  @Override
  public Predicate toPredicate(
      Root<PaymentMethod> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    List<Predicate> predicates = new ArrayList<>();

    if (StringUtils.isNotEmpty(name)) {
      predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
    }

    if (Objects.nonNull(transactionFee)) {
      predicates.add(criteriaBuilder.equal(root.get("transactionFee"), transactionFee));
    }

    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTransactionFee(BigDecimal transactionFee) {
    this.transactionFee = transactionFee;
  }
}
