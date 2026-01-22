package com.abn.recipes.domain.dtos;

import java.util.List;

public record RecipeSearchRequest(
        Boolean vegetarian,
        Integer servings,
        List<String> include,
        List<String> exclude,
        String search
) {}

