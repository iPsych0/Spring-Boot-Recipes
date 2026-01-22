package com.abn.recipes.controllers;


import com.abn.recipes.domain.models.Recipe;
import com.abn.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class RecipeControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecipeRepository recipeRepository;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.active.profiles", () -> "dev");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }


    @BeforeEach
    void setup() {
        recipeRepository.deleteAll();
    }


    @Test
    void createRecipe_shouldPersistAndReturnCreated() throws Exception {
        String json = """
                {
                    "name": "Pasta",
                    "servings": 2,
                    "vegetarian": true,
                    "ingredients": "noodles, tomato",
                    "instructions": "boil water"
                }
                """;

        mockMvc.perform(post("/api/v1/recipes")
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andExpect(jsonPath("$.vegetarian").value(true));
    }

    @Test
    void getRecipeById_shouldReturnRecipe() throws Exception {
        Recipe saved = createRecipe("Cake", 4, false, "flour, sugar", "mix and bake");

        mockMvc.perform(get("/api/v1/recipes/" + saved.getId())
                        .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cake"))
                .andExpect(jsonPath("$.servings").value(4));
    }

    @Test
    void getRecipes_shouldFilterUsingSpecifications() throws Exception {
        createRecipe("Mushroom Pizza", 2, true, "mushroom, cheese, tomato", "bake");
        createRecipe("Chicken Curry", 4, false, "chicken, spices", "cook");
        createRecipe("Tomato Soup", 2, true, "tomato, water", "boil");

        mockMvc.perform(get("/api/v1/recipes")
                        .with(httpBasic("user", "password"))
                        .param("vegetarian", "true")
                        .param("servings", "2")
                        .param("include", "tomato")
                        .param("exclude", "meat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))    // Pizza + Soup
                .andExpect(jsonPath("$[0].vegetarian").value(true))
                .andExpect(jsonPath("$[1].vegetarian").value(true));
    }

    @Test
    void updateRecipe_shouldModifyExistingRecipe() throws Exception {
        Recipe saved = createRecipe("Soup", 3, true, "veg", "boil");

        String json = """
                {
                    "name": "Updated Soup",
                    "servings": 4,
                    "vegetarian": true,
                    "ingredients": "veg, salt",
                    "instructions": "heat"
                }
                """;

        mockMvc.perform(put("/api/v1/recipes/" + saved.getId())
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.name").value("Updated Soup"))
                .andExpect(jsonPath("$.servings").value(4));
    }


    @Test
    void deleteRecipe_shouldRemoveFromDatabase() throws Exception {
        Recipe saved = createRecipe("Burger", 1, false, "meat, bun", "cook it");

        mockMvc.perform(delete("/api/v1/recipes/" + saved.getId())
                        .with(httpBasic("user", "password")))
                .andExpect(status().isOk());

        assertThat(recipeRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void requestWithoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/recipes"))
                .andExpect(status().isUnauthorized());
    }

    private Recipe createRecipe(
            String name, int servings, boolean vegetarian,
            String ingredients, String instructions) {

        return recipeRepository.save(
                Recipe.builder()
                        .name(name)
                        .servings(servings)
                        .vegetarian(vegetarian)
                        .ingredients(ingredients)
                        .instructions(instructions)
                        .build()
        );
    }
}

