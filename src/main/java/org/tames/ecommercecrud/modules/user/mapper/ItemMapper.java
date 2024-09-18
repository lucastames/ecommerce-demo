package org.tames.ecommercecrud.modules.user.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.product.entity.Product;
import org.tames.ecommercecrud.modules.user.dto.ItemRequestDto;
import org.tames.ecommercecrud.modules.user.dto.ItemResponseDto;
import org.tames.ecommercecrud.modules.user.entity.Item;

@Component
public class ItemMapper {

  public ItemResponseDto toDto(Item item) {
    return new ItemResponseDto(
        item.getUnitPrice(), item.getQuantity(), item.getProduct().getName());
  }

  public List<ItemResponseDto> toDto(List<Item> items) {
    return items.stream().map(this::toDto).toList();
  }

  public Item toEntity(ItemRequestDto itemRequestDto, Product product) {
    return new Item(product.getPrice(), itemRequestDto.quantity(), product);
  }
}
