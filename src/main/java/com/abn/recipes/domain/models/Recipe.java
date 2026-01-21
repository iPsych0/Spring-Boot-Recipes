package com.abn.recipes.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "recipes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String ingredients;

    @Column(name = "instructions", nullable = false)
    private String instructions;

}
