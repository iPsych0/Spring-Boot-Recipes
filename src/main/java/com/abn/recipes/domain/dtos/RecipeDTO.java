package com.abn.recipes.domain.dtos;

import com.abn.recipes.domain.models.Recipe;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RecipeDTO(
        UUID id,
        @NotNull String name,
        @NotNull Integer servings,
        @NotNull Boolean vegetarian,
        @NotNull String ingredients,
        @NotNull String instructions
) {
    public static Recipe toEntity(RecipeDTO dto) {
        return Recipe.builder()
                .name(dto.name())
                .servings(dto.servings())
                .vegetarian(dto.vegetarian())
                .ingredients(dto.ingredients())
                .instructions(dto.instructions())
                .build();
    }

    public static RecipeDTO fromEntity(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getServings(),
                recipe.getVegetarian(),
                recipe.getIngredients(),
                recipe.getInstructions()
        );
    }

}
