package com.example.demo.repository;

import com.example.demo.models.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrinksRepository extends JpaRepository<Drink, Long> {

    Optional<Drink> findByDrinkName(String drinkName);
    Optional<Drink> findTopByOrderByDrinkMadeCountDesc();
    List<Drink> findAll();
}
