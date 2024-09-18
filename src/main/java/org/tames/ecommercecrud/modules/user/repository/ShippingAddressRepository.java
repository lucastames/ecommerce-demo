package org.tames.ecommercecrud.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.tames.ecommercecrud.modules.user.entity.ShippingAddress;

public interface ShippingAddressRepository
    extends JpaRepository<ShippingAddress, Long>, JpaSpecificationExecutor<ShippingAddress> {}
