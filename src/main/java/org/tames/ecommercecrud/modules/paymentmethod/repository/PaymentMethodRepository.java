package org.tames.ecommercecrud.modules.paymentmethod.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.tames.ecommercecrud.modules.paymentmethod.entity.PaymentMethod;

public interface PaymentMethodRepository
    extends JpaRepository<PaymentMethod, Long>, JpaSpecificationExecutor<PaymentMethod> {}
