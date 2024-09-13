package org.tames.ecommercecrud.modules.category.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.category.entity.Category;

public class CategorySpecs {
  public static class CategoryFilter implements Specification<Category> {
    private final String name;

    public CategoryFilter(String name) {
      this.name = name;
    }

    @Override
    public Predicate toPredicate(
        Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
      List<Specification<Category>> specifications = new ArrayList<>();

      if (StringUtils.isNotEmpty(name)) {
        specifications.add(byName(name));
      }

      return Specification.allOf(specifications).toPredicate(root, query, criteriaBuilder);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CategoryFilter that)) return false;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(name);
    }
  }

  public static Specification<Category> byName(String name) {
    return (root, cq, cb) -> cb.like(root.get("name"), "%" + name + "%");
  }
}
