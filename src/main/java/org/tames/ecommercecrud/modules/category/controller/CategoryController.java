package org.tames.ecommercecrud.modules.category.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.SaveCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.service.CategoryService;
import org.tames.ecommercecrud.modules.category.specification.CategorySpecs.CategoryFilter;

@RestController
@RequestMapping("categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<PagedModel<CategoryResponseDto>> getCategories(
      Pageable pageable, CategoryFilter categoryFilter) {
    return ResponseEntity.ok(categoryService.getCategories(pageable, categoryFilter));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponseDto> createCategory(
      @RequestBody @Valid SaveCategoryRequestDto saveCategoryRequestDto, UriComponentsBuilder ucb) {
    CategoryResponseDto createdCategory = categoryService.createCategory(saveCategoryRequestDto);

    return ResponseEntity.created(ucb.path("/categories/{categoryId}").build(createdCategory.id()))
        .body(createdCategory);
  }

  @GetMapping("{categoryId}")
  public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long categoryId) {
    return ResponseEntity.ok(categoryService.getCategory(categoryId));
  }

  @PutMapping("{categoryId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponseDto> updateCategory(
      @RequestBody @Valid SaveCategoryRequestDto saveCategoryRequestDto,
      @PathVariable Long categoryId) {
    return ResponseEntity.ok(categoryService.updateCategory(saveCategoryRequestDto, categoryId));
  }

  @DeleteMapping("{categoryId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
    categoryService.deleteCategory(categoryId);

    return ResponseEntity.noContent().build();
  }
}
