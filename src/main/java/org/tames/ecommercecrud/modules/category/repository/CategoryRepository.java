package org.tames.ecommercecrud.modules.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
