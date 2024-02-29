package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Integer> {
    Optional<Food> findByNameOfFood(String name);
}
