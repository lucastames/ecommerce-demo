package org.tames.ecommercecrud.modules.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.SaveCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.entity.Category;
import org.tames.ecommercecrud.modules.category.exception.CategoryNotFoundException;
import org.tames.ecommercecrud.modules.category.mapper.CategoryMapper;
import org.tames.ecommercecrud.modules.category.repository.CategoryRepository;
import org.tames.ecommercecrud.modules.category.specification.CategorySpecs.CategoryFilter;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
  @Mock CategoryRepository categoryRepository;
  @Mock CategoryMapper categoryMapper;

  @InjectMocks CategoryService categoryService;

  SaveCategoryRequestDto categoryRequestDto;
  Category mappedCategory;
  Category persistedCategory;
  CategoryResponseDto categoryResponseDto;

  @BeforeEach
  void setUp() {
    categoryRequestDto = new SaveCategoryRequestDto("Category 1");

    mappedCategory = new Category("Category 1");

    persistedCategory = new Category("Category 1");
    persistedCategory.setId(1L);

    categoryResponseDto = new CategoryResponseDto(1L, "Category 1");
  }

  @Test
  void testGetCategories_WhenPageParamsAndFilterAreProvided_ShouldReturnPagedModelWithCategories() {
    Pageable pageable = PageRequest.of(0, 50, Sort.by("name"));
    CategoryFilter categoryFilter = new CategoryFilter("Category 1");

    given(categoryRepository.findAll(categoryFilter, pageable))
        .willReturn(new PageImpl<>(List.of(persistedCategory)));
    given(categoryMapper.toDto(persistedCategory)).willReturn(categoryResponseDto);

    PagedModel<CategoryResponseDto> result =
        categoryService.getCategories(pageable, categoryFilter);

    assertThat(result).isNotNull();
    assertThat(result.getContent())
        .isNotNull()
        .hasSize(1)
        .first()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(categoryResponseDto.id(), categoryResponseDto.name());
    ;
  }

  @Test
  void testGetCategory_WhenExistingCategoryIdIsProvided_ShouldReturnCategoryResponseDto() {
    given(categoryRepository.findById(1L)).willReturn(Optional.of(persistedCategory));
    given(categoryMapper.toDto(persistedCategory)).willReturn(categoryResponseDto);

    CategoryResponseDto result = categoryService.getCategory(1L);

    assertThat(result)
        .isNotNull()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(categoryResponseDto.id(), categoryResponseDto.name());
  }

  @Test
  void testGetCategory_WhenNonExistingCategoryIdIsProvided_ShouldThrowCategoryNotFoundException() {
    given(categoryRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.getCategory(99L))
        .isInstanceOf(CategoryNotFoundException.class);
  }

  @Test
  void
      testCreateCategory_WhenValidCategoryRequestDtoIsProvided_ShouldReturnCreatedCategoryResponseDto() {
    given(categoryMapper.toEntity(categoryRequestDto)).willReturn(mappedCategory);
    given(categoryRepository.save(mappedCategory)).willReturn(persistedCategory);
    given(categoryMapper.toDto(persistedCategory)).willReturn(categoryResponseDto);

    CategoryResponseDto result = categoryService.createCategory(categoryRequestDto);

    assertThat(result)
        .isNotNull()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(categoryResponseDto.id(), categoryResponseDto.name());
  }

  @Test
  void
      testUpdateCategory_WhenValidCategoryRequestDtoAndCategoryIdIsProvided_ShouldReturnUpdatedCategoryResponseDto() {
    CategoryResponseDto updatedCategoryResponseDto =
        new CategoryResponseDto(1L, "Category updated");
    SaveCategoryRequestDto updatedCategoryRequestDto =
        new SaveCategoryRequestDto("Category updated");
    Category updatedCategory = new Category("Category updated");
    updatedCategory.setId(1L);

    given(categoryRepository.findById(1L)).willReturn(Optional.of(persistedCategory));
    given(categoryRepository.save(persistedCategory)).willReturn(updatedCategory);
    given(categoryMapper.toDto(updatedCategory)).willReturn(updatedCategoryResponseDto);

    CategoryResponseDto result = categoryService.updateCategory(updatedCategoryRequestDto, 1L);
    assertThat(result)
        .isNotNull()
        .extracting(CategoryResponseDto::id, CategoryResponseDto::name)
        .containsExactly(updatedCategoryResponseDto.id(), updatedCategoryResponseDto.name());

    then(categoryMapper).should().updateFromDto(persistedCategory, updatedCategoryRequestDto);
  }

  @Test
  void
      testUpdatedCategory_WhenNonExistingCategoryIdIsProvided_ShouldThrowCategoryNotFoundException() {
    given(categoryRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.updateCategory(categoryRequestDto, 99L))
        .isInstanceOf(CategoryNotFoundException.class);
  }

  @Test
  void testDeleteCategory_WhenExistingCategoryIdIsProvided_ShouldDeleteTheCategory() {
    given(categoryRepository.findById(1L)).willReturn(Optional.of(persistedCategory));

    categoryService.deleteCategory(1L);

    then(categoryRepository).should().delete(persistedCategory);
  }

  @Test
  void
      testDeleteCategory_WhenNonExistingCategoryIdIsProvided_ShouldThrowCategoryNotFoundException() {
    given(categoryRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.deleteCategory(99L))
        .isInstanceOf(CategoryNotFoundException.class);
  }
}
