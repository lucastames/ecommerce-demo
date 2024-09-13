package org.tames.ecommercecrud.modules.category.mapper;

import org.springframework.stereotype.Component;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.SaveCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.entity.Category;

@Component
public class CategoryMapper {
  public CategoryResponseDto toDto(Category category) {
    return new CategoryResponseDto(category.getId(), category.getName());
  }

  public Category toEntity(SaveCategoryRequestDto saveCategoryRequestDto) {
    return new Category(saveCategoryRequestDto.name());
  }

  public void updateFromDto(Category category, SaveCategoryRequestDto saveCategoryRequestDto) {
    category.setName(saveCategoryRequestDto.name());
  }
}
