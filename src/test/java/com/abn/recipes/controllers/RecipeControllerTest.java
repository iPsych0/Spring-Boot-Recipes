package com.abn.recipes.controllers;

import com.abn.recipes.domain.dtos.RecipeDTO;
import com.abn.recipes.domain.dtos.RecipeSearchRequest;
import com.abn.recipes.domain.models.Recipe;
import com.abn.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeController recipeController;

    private Recipe recipe;
    private RecipeDTO recipeDTO;
    private UUID recipeId;
    private RecipeSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        recipe = Recipe.builder()
                .id(recipeId)
                .name("Pasta")
                .servings(4)
                .vegetarian(true)
                .ingredients("Pasta, Tomato, Cheese")
                .instructions("Boil pasta, add sauce")
                .build();

        recipeDTO = RecipeDTO.fromEntity(recipe);
        searchRequest = new RecipeSearchRequest(
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void createRecipe_shouldReturnCreated() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        var response = recipeController.createRecipe(recipeDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Pasta", response.getBody().name());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void getRecipes_shouldReturnAllRecipes() {
        when(recipeRepository.findAll(any(Specification.class))).thenReturn(List.of(recipe));

        var response = recipeController.getRecipes(searchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(recipeRepository).findAll(any(Specification.class));
    }

    @Test
    void getRecipeById_shouldReturnRecipe() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        var response = recipeController.getRecipeById(recipeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipeId, response.getBody().id());
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void getRecipeById_shouldThrow404WhenNotFound() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class,
                () -> recipeController.getRecipeById(recipeId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void updateRecipe_shouldReturnUpdatedRecipe() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        var response = recipeController.updateRecipe(recipeId, recipeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipeId, response.getBody().id());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_shouldThrow404WhenNotFound() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class,
                () -> recipeController.updateRecipe(recipeId, recipeDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void deleteRecipe_shouldDeleteSuccessfully() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        var response = recipeController.deleteRecipe(recipeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipe_shouldThrow404WhenNotFound() {
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class,
                () -> recipeController.deleteRecipe(recipeId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }
}