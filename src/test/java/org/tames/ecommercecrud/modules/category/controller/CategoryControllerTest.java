package org.tames.ecommercecrud.modules.category.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.tames.ecommercecrud.annotations.WithMockAdmin;
import org.tames.ecommercecrud.annotations.WithMockCustomer;
import org.tames.ecommercecrud.config.ControllerTestConfig;
import org.tames.ecommercecrud.modules.category.dto.CategoryResponseDto;
import org.tames.ecommercecrud.modules.category.dto.SaveCategoryRequestDto;
import org.tames.ecommercecrud.modules.category.exception.CategoryNotFoundException;
import org.tames.ecommercecrud.modules.category.service.CategoryService;

@WebMvcTest(CategoryController.class)
@Import(ControllerTestConfig.class)
@WithMockAdmin
public class CategoryControllerTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean CategoryService categoryService;

  SaveCategoryRequestDto categoryRequestDto;
  CategoryResponseDto categoryResponseDto;
  SaveCategoryRequestDto invalidCategoryRequestDto;

  @BeforeEach
  void setUp() {
    categoryRequestDto = new SaveCategoryRequestDto("New category");
    invalidCategoryRequestDto = new SaveCategoryRequestDto("");
    categoryResponseDto = new CategoryResponseDto(1L, "New category");
  }

  @Test
  void
      testGetCategories_WhenProvidingAllParameters_ShouldReturnPagedModelWithCategoriesAndOkStatus() {}

  @Test
  void testCreateCategory_WhenValidDtoIsProvided_ShouldReturnCreatedCategoryAndCreatedStatus()
      throws Exception {
    given(categoryService.createCategory(categoryRequestDto)).willReturn(categoryResponseDto);

    mockMvc
        .perform(
            post("/categories")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
        .andExpectAll(
            status().isCreated(),
            header().string("Location", containsString("/categories/1")),
            jsonPath("$.id").value(categoryResponseDto.id()),
            jsonPath("$.name").value(categoryResponseDto.name()));
  }

  @Test
  void testCreateCategory_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            post("/categories")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategoryRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockCustomer
  void testCreateCategory_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            post("/categories")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetCategory_WhenExistingCategoryIdIsProvided_ShouldReturnCategoryAndOkStatus()
      throws Exception {
    given(categoryService.getCategory(1L)).willReturn(categoryResponseDto);

    mockMvc
        .perform(get("/categories/{categoryId}", 1L).accept(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.name").value(categoryResponseDto.name()),
            jsonPath("$.id").value(categoryResponseDto.id()));
  }

  @Test
  void testGetCategory_WhenNonExistingCategoryIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(categoryService.getCategory(99L)).willThrow(new CategoryNotFoundException(99L));

    mockMvc
        .perform(get("/categories/{categoryId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateCategory_WhenValidDtoIsProvided_ShouldReturnUpdatedCategoryAndOkStatus()
      throws Exception {
    given(categoryService.updateCategory(categoryRequestDto, 1L)).willReturn(categoryResponseDto);

    mockMvc
        .perform(
            put("/categories/{categoryId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.name").value(categoryResponseDto.name()),
            jsonPath("$.id").value(categoryResponseDto.id()));
  }

  @Test
  void testUpdateCategory_WhenNonExistingCategoryIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    given(categoryService.updateCategory(categoryRequestDto, 99L))
        .willThrow(new CategoryNotFoundException(99L));

    mockMvc
        .perform(
            put("/categories/{categoryId}", 99L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateCategory_WhenInvalidDtoIsProvided_ShouldReturnBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            put("/categories/{categoryId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategoryRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockCustomer
  void testUpdateCategory_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(
            put("/categories/{categoryId}", 1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteCategory_WhenExsitingCategoryIdIsProvided_ShouldReturnNoContentStatus()
      throws Exception {
    mockMvc
        .perform(delete("/categories/{categoryId}", 1L).accept(APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(categoryService).should().deleteCategory(1L);
  }

  @Test
  void testDeleteCategory_WhenNonExistingCategoryIdIsProvided_ShouldReturnNotFoundStatus()
      throws Exception {
    willThrow(new CategoryNotFoundException(99L)).given(categoryService).deleteCategory(99L);

    mockMvc
        .perform(delete("/categories/{categoryId}", 99L).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockCustomer
  void testDeleteCategory_WhenUserDoesntHaveAuthorization_ShouldReturnForbiddenStatus()
      throws Exception {
    mockMvc
        .perform(delete("/categories/{categoryId}", 1L).accept(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
