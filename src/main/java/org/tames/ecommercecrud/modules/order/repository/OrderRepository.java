package org.tames.ecommercecrud.modules.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
