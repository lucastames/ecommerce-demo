package org.tames.ecommercecrud.modules.user.specification;

import org.springframework.data.jpa.domain.Specification;
import org.tames.ecommercecrud.modules.user.entity.ShippingAddress;

public class ShippingAddressSpecs {
  public static Specification<ShippingAddress> byUserId(Long userId) {
    return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
  }
}
