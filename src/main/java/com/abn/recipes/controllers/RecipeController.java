package com.abn.recipes.controllers;

import com.abn.recipes.domain.dtos.RecipeDTO;
import com.abn.recipes.domain.dtos.RecipePatchDTO;
import com.abn.recipes.domain.dtos.RecipeSearchRequest;
import com.abn.recipes.repositories.RecipeRepository;
import com.abn.recipes.repositories.specs.RecipeSpecification;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.abn.recipes.controllers.RecipeController.BASE_PATH;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(BASE_PATH)
public class RecipeController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    public static final String BASE_PATH = "/api/v1/recipes";
    public static final String ID_NOT_FOUND_MSG = "Recipe not found: %s";

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        log.info("Creating new recipe: {}", recipeDTO.name());
        var saved = recipeRepository.save(RecipeDTO.toEntity(recipeDTO));
        log.debug("Recipe created with id: {}", saved.getId());
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + saved.getId()))
                .body(RecipeDTO.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getRecipes(RecipeSearchRequest filters) {
        log.debug("Searching recipes with filters: {}", filters);
        var spec = RecipeSpecification.build(filters);
        var recipes = recipeRepository.findAll(spec);
        var recipeDTOs = recipes.stream()
                .map(RecipeDTO::fromEntity)
                .toList();
        log.debug("Found {} recipes", recipeDTOs.size());
        return ResponseEntity.ok(recipeDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable UUID id) {
        log.debug("Fetching recipe by id: {}", id);
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));
        return ResponseEntity.ok(RecipeDTO.fromEntity(recipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody RecipeDTO recipeDTO
    ) {
        log.info("Updating recipe: {}", id);
        var found = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));

        var toUpdate = RecipeDTO.toEntity(recipeDTO, found);
        var saved = recipeRepository.save(toUpdate);
        log.debug("Recipe updated: {}", id);

        return ResponseEntity.ok(RecipeDTO.fromEntity(saved));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RecipeDTO> patchRecipe(
            @PathVariable UUID id,
            @RequestBody RecipePatchDTO recipePatchDTO
    ) {
        log.info("Patching recipe: {}", id);
        var found = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));

        var patched = RecipePatchDTO.applyPatch(found, recipePatchDTO);
        var saved = recipeRepository.save(patched);
        log.debug("Recipe patched: {}", id);

        return ResponseEntity.ok(RecipeDTO.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        log.info("Deleting recipe: {}", id);
        var existing = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ID_NOT_FOUND_MSG.formatted(id)));

        recipeRepository.delete(existing);
        log.debug("Recipe deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}


