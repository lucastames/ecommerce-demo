package org.tames.ecommercecrud.modules.product.mapper;

import java.util.Set;
import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.mapper.CategoryMapper;
import org.tames.ecommercecrud.modules.product.dto.ProductResponseDto;
import org.tames.ecommercecrud.modules.product.dto.SaveProductRequestDto;
import org.tames.ecommercecrud.modules.product.entity.Product;

@Component
public class ProductMapper {
  private final CategoryMapper categoryMapper;

  public ProductMapper(CategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
  }

  public ProductResponseDto toDto(Product product) {
    return new ProductResponseDto(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getDescription(),
        product.getStockQuantity(),
        product.getCategories().stream().map(categoryMapper::toDto).toList());
  }

  public Product toEntity(SaveProductRequestDto saveProductRequestDto, Set<Category> categories) {
    Product product =
        new Product(
            saveProductRequestDto.name(),
            saveProductRequestDto.price(),
            saveProductRequestDto.description(),
            saveProductRequestDto.stockQuantity());

    categories.forEach(product::addCategory);

    return product;
  }

  public void updateFromDto(
      Product product, SaveProductRequestDto saveProductRequestDto, Set<Category> categories) {
    product.setDescription(saveProductRequestDto.description());
    product.setName(saveProductRequestDto.name());
    product.setPrice(saveProductRequestDto.price());
    product.setStockQuantity(saveProductRequestDto.stockQuantity());

    product.getCategories().retainAll(categories);
    product.getCategories().addAll(categories);
  }
}
