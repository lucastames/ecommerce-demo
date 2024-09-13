package org.tames.ecommercecrud.modules.category.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.SaveCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.exception.CategoryNotFoundException;
import org.tames.ecommercecrud.modules.category.mapper.CategoryMapper;
import org.tames.ecommercecrud.modules.category.repository.CategoryRepository;
import org.tames.ecommercecrud.modules.category.specification.CategorySpecs.CategoryFilter;

@Service
@Transactional(readOnly = true)
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  public PagedModel<CategoryResponseDto> getCategories(
      Pageable pageable, CategoryFilter categoryFilter) {
    Page<CategoryResponseDto> categoryPage =
        categoryRepository.findAll(categoryFilter, pageable).map(categoryMapper::toDto);

    return new PagedModel<>(categoryPage);
  }

  public CategoryResponseDto getCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    return categoryMapper.toDto(category);
  }

  @Transactional
  public CategoryResponseDto createCategory(SaveCategoryRequestDto saveCategoryRequestDto) {
    Category category = categoryMapper.toEntity(saveCategoryRequestDto);
    return categoryMapper.toDto(categoryRepository.save(category));
  }

  @Transactional
  public CategoryResponseDto updateCategory(
      SaveCategoryRequestDto saveCategoryRequestDto, Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    categoryMapper.updateFromDto(category, saveCategoryRequestDto);

    return categoryMapper.toDto(categoryRepository.save(category));
  }

  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    categoryRepository.delete(category);
  }
}
