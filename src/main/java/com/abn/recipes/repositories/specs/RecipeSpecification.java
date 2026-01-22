package com.abn.recipes.repositories.specs;

import com.abn.recipes.domain.dtos.RecipeSearchRequest;
import com.abn.recipes.domain.models.Recipe;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecification {

    public static Specification<Recipe> vegetarian(Boolean vegetarian) {
        return (root, _, cb) ->
                vegetarian == null ? null : cb.equal(root.get("vegetarian"), vegetarian);
    }

    public static Specification<Recipe> servings(Integer servings) {
        return (root, _, cb) ->
                servings == null ? null : cb.equal(root.get("servings"), servings);
    }

    public static Specification<Recipe> includeIngredients(List<String> ingredients) {
        return (root, _, cb) -> {
            if (ingredients == null || ingredients.isEmpty()) return null;
            return ingredients.stream()
                    .map(ingredient -> cb.like(cb.lower(root.get("ingredients")), "%" + ingredient.toLowerCase() + "%"))
                    .reduce(cb::and)
                    .orElse(null);
        };
    }

    public static Specification<Recipe> excludeIngredients(List<String> ingredients) {
        return (root, _, cb) -> {
            if (ingredients == null || ingredients.isEmpty()) return null;
            return ingredients.stream()
                    .map(ingredient -> cb.notLike(cb.lower(root.get("ingredients")), "%" + ingredient.toLowerCase() + "%"))
                    .reduce(cb::and)
                    .orElse(null);
        };
    }

    public static Specification<Recipe> textSearch(String text) {
        return (root, _, cb) ->
                text == null ? null : cb.like(cb.lower(root.get("instructions")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Recipe> build(RecipeSearchRequest req) {
        return Specification
                .where(vegetarian(req.vegetarian()))
                .and(servings(req.servings()))
                .and(includeIngredients(req.include()))
                .and(excludeIngredients(req.exclude()))
                .and(textSearch(req.search()));
    }
}

