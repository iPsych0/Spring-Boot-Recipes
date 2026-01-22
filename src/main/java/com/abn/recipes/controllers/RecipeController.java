package com.abn.recipes.controllers;

import com.abn.recipes.domain.dtos.RecipeDTO;
import com.abn.recipes.repositories.RecipeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.abn.recipes.controllers.RecipeController.BASE_PATH;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH)
public class RecipeController {

    public static final String BASE_PATH = "/api/v1/recipes";
    public static final String ID_NOT_FOUND_MSG = "Recipe not found: %s";
    private final RecipeRepository recipeRepository;

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        var saved = recipeRepository.save(RecipeDTO.toEntity(recipeDTO));
        return ResponseEntity
                .created(URI.create(BASE_PATH + saved.getId()))
                .body(RecipeDTO.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getRecipes() {
        var recipes = recipeRepository.findAll();
        var recipeDTOs = recipes.stream()
                .map(RecipeDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(recipeDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable UUID id) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));
        return ResponseEntity.ok(RecipeDTO.fromEntity(recipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeDTO recipeDTO
    ) {
        var found = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));

        // Convert DTO to entity and ensure ID is preserved
        var toUpdate = RecipeDTO.toEntity(recipeDTO, found.getId());
        var saved = recipeRepository.save(toUpdate);

        return ResponseEntity.ok(RecipeDTO.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        var existing = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));

        recipeRepository.delete(existing);
        return ResponseEntity.ok().build();
    }

}
