package com.abn.recipes.models;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
