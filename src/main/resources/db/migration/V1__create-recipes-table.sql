--liquibase formatted sql

--changeset daniel:001-create-recipes-table
CREATE TABLE recipes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    servings INTEGER NOT NULL,
    vegetarian BOOLEAN NOT NULL,
    ingredients VARCHAR(4096) NOT NULL,
    instructions VARCHAR(4096) NOT NULL
);