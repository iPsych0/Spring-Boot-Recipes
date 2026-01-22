package com.abn.recipes.repositories;

import com.abn.recipes.domain.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {
}
