package com.example.demo.service;

import com.example.demo.exceptions.DrinkNotFound;
import com.example.demo.exceptions.NotEnoughIngredientException;
import com.example.demo.models.Drink;
import com.example.demo.models.Ingredient;
import com.example.demo.models.Recipe;
import com.example.demo.repository.DrinksRepository;
import com.example.demo.repository.IngredientRepository;
import com.example.demo.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CoffeeMachineService {

    private final  DrinksRepository drinksRepository;
    private final  IngredientRepository ingredientRepository;
    private final  RecipeRepository recipeRepository;

    public CoffeeMachineService(
            DrinksRepository drinksRepository ,
            IngredientRepository ingredientRepository ,
            RecipeRepository recipeRepository
    ){
        this.drinksRepository = drinksRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public String makeDrink(String drinkName){

        drinkName = normalizeDrinkName(drinkName);
        Drink drink = findDrinkByName(drinkName);
        List<Recipe> recipes = recipeRepository.findByDrink_DrinkId(drink.getDrinkId());

        checkIngredientAmount(recipes);
        saveNewIngredientAmount(recipes);
        countMadeDrinks(drink);

        return  "Ваш "+ drinkName + " готов!";

    }

    public String normalizeDrinkName(String drinkName){
        return drinkName.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public Drink findDrinkByName(String drinkName){

        return drinksRepository.findByDrinkName(drinkName)
                .orElseThrow(()-> new DrinkNotFound("К сожалению , кофемашина не изготавливает данный напиток"));
    }

    public void checkIngredientAmount(List<Recipe> recipes){

        for(Recipe recipe : recipes){

            Ingredient ingredient = recipe.getIngredient();

            if(ingredient.getIngredientCurrentAmount() < recipe.getIngredientAmount()){
                throw new NotEnoughIngredientException("Ингредиента " + ingredient.getIngredientName() + " недостаточно");
            }

        }

    }

    public void saveNewIngredientAmount(List<Recipe> recipes){

        List<Ingredient> updatedIngrediensAmount = recipes.stream()
                .map(recipe -> {
                    Ingredient ingredient = recipe.getIngredient();
                    ingredient.setIngredientCurrentAmount(
                            ingredient.getIngredientCurrentAmount() - recipe.getIngredientAmount()
                    );

                    return  ingredient;
                })
                .collect(Collectors.toList());

        ingredientRepository.saveAll(updatedIngrediensAmount);

    }

    public void countMadeDrinks(Drink drink){
        drink.setDrinkMadeCount(drink.getDrinkMadeCount() + 1);
        drinksRepository.save(drink);
    }

    public String getPopularDrink() {
        return drinksRepository.findTopByOrderByDrinkMadeCountDesc()
                .map(Drink::getDrinkName)
                .orElse("На данный момент все напитки одинаково часто заказываются");
    }

    public List<Recipe> addNewRecipe(String newDrinkName ,
                               List<String> ingredients ,
                               List<Integer> amountsForRecipe,
                                     List<Integer> newIngredientAmount){

        Drink newDrink = addNewDrink(newDrinkName);

        List<Recipe> newRecipesList = new ArrayList<>();
        AtomicInteger newIngredientIndex = new AtomicInteger(0);

        for(int i = 0 ; i < ingredients.size() ; i++){

            String ingredientName = ingredients.get(i);
            int amountForRecipe = amountsForRecipe.get(i);

            Ingredient newIngredient = addNewIngredient(ingredientName, newIngredientAmount , newIngredientIndex);
            Recipe recipe = makeNewRecipe(newDrink , newIngredient , amountForRecipe);

            newRecipesList.add(recipe);

        }

        return newRecipesList;

    }

    public Drink addNewDrink(String newDrinkName){

        return drinksRepository.findByDrinkName(newDrinkName)
                .orElseGet(()-> drinksRepository.save(
                        Drink.builder()
                                .drinkName(newDrinkName)
                                .drinkMadeCount(0)
                                .build()));

    }

    public Ingredient addNewIngredient(String ingredientName
            ,List<Integer> newIngredientAmountForRecipe
            ,AtomicInteger newIngredientIndex){

        return ingredientRepository.findByIngredientName(ingredientName)
                .orElseGet(()->{
                    int initialAmount = newIngredientAmountForRecipe.get(newIngredientIndex.getAndIncrement());
                    return ingredientRepository.save(
                            Ingredient.builder()
                                    .ingredientName(ingredientName)
                                    .ingredientCurrentAmount(initialAmount)
                                    .build());
                });
    }

    public Recipe makeNewRecipe(Drink newDrink , Ingredient newIngredient , int amountForRecipe){

        return recipeRepository.save(
                Recipe.builder()
                        .drink(newDrink)
                        .ingredient(newIngredient)
                        .ingredientAmount(amountForRecipe)
                        .build());

    }

}
