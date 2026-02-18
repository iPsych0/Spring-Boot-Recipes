package com.abn.recipes.domain.dtos;

import com.abn.recipes.domain.models.Recipe;

public record RecipePatchDTO(
        String name,
        Integer servings,
        Boolean vegetarian,
        String ingredients,
        String instructions
) {

    public static Recipe applyPatch(Recipe existing, RecipePatchDTO patch) {
        return Recipe.builder()
                .id(existing.getId())
                .name(patch.name() != null ? patch.name() : existing.getName())
                .servings(patch.servings() != null ? patch.servings() : existing.getServings())
                .vegetarian(patch.vegetarian() != null ? patch.vegetarian() : existing.getVegetarian())
                .ingredients(patch.ingredients() != null ? patch.ingredients() : existing.getIngredients())
                .instructions(patch.instructions() != null ? patch.instructions() : existing.getInstructions())
                .build();
    }

}
