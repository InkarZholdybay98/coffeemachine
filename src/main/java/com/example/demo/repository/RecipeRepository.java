    package com.example.demo.repository;

    import com.example.demo.models.Recipe;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;
    import java.util.List;

    @Repository
    public interface RecipeRepository extends JpaRepository<Recipe , Long> {

        @Query("SELECT r FROM Recipe r WHERE r.drink.drinkId = :drinkId")
        List<Recipe> findByDrink_DrinkId(long drinkId);

    }
