package com.abn.recipes.ports.out;

import com.abn.recipes.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
}
