package org.tames.ecommercecrud.modules.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
