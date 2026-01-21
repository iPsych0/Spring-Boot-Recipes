package com.abn.recipes.controllers;

import com.abn.recipes.domain.dtos.RecipeDTO;
import com.abn.recipes.repositories.RecipeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        var saved = recipeRepository.save(RecipeDTO.toEntity(recipeDTO));
        return ResponseEntity.ok(RecipeDTO.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getRecipes() {
        var recipes = recipeRepository.findAll();
        var recipeDTOs = recipes.stream()
                .map(RecipeDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(recipeDTOs);
    }

}
