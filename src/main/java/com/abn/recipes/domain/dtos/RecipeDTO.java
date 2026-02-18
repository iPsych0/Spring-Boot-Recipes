package com.abn.recipes.domain.dtos;

import com.abn.recipes.domain.models.Recipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public record RecipeDTO(
        UUID id,
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Servings is required") @Positive(message = "Servings must be positive") Integer servings,
        @NotNull(message = "Vegetarian flag is required") Boolean vegetarian,
        @NotBlank(message = "Ingredients are required") String ingredients,
        @NotBlank(message = "Instructions are required") String instructions,
        Instant createdAt,
        Instant updatedAt
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

    public static Recipe toEntity(RecipeDTO dto, Recipe existing) {
        return Recipe.builder()
                .id(existing.getId())
                .version(existing.getVersion())
                .name(dto.name())
                .servings(dto.servings())
                .vegetarian(dto.vegetarian())
                .ingredients(dto.ingredients())
                .instructions(dto.instructions())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public static RecipeDTO fromEntity(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getServings(),
                recipe.getVegetarian(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }
}
