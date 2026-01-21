package com.abn.recipes.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.Set;
import java.util.UUID;

@Entity
public class Recipe {
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "servings", nullable = false)
    private Integer servings;

    @Column(name = "vegetarian", nullable = false)
    private Boolean vegetarian;

    @Column(name = "ingredients", nullable = false)
    private Set<String> ingredients;

    @Column(name = "instructions", nullable = false)
    private String instructions;

}
