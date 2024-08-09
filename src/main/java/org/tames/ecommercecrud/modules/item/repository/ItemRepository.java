package org.tames.ecommercecrud.modules.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.item.entity.Item;
import org.tames.ecommercecrud.modules.item.pk.ItemPk;

public interface ItemRepository extends JpaRepository<Item, ItemPk> {}
