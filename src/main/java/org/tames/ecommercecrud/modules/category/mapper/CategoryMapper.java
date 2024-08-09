package org.tames.ecommercecrud.modules.category.mapper;

import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.CreateCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.entity.Category;

@Component
public class CategoryMapper {
  public CategoryResponseDto toDto(Category category) {
    return new CategoryResponseDto(category.getId(), category.getName());
  }

  public Category toEntity(CreateCategoryRequestDto createCategoryRequestDto) {
    return new Category(createCategoryRequestDto.name());
  }
}
