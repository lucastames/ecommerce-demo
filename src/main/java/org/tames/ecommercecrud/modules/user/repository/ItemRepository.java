package org.tames.ecommercecrud.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.user.entity.Item;
import org.tames.ecommercecrud.modules.user.pk.ItemPk;

public interface ItemRepository extends JpaRepository<Item, ItemPk> {}
