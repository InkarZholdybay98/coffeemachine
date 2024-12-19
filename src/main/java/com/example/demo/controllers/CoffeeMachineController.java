package com.example.demo.controllers;

import com.example.demo.models.Recipe;
import com.example.demo.service.CoffeeMachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/coffeemachine")
@Tag(name = "Coffee machine", description = "API для управления кофемашиной")
public class CoffeeMachineController {

    private final CoffeeMachineService coffeeMachineService;

    public CoffeeMachineController(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @PostMapping("/make")
    @Operation(summary = "Приготовить напиток",
                description = "Приготовит указанный напиток при наличии необходимых ингредиентов")
    public ResponseEntity<String> makeDrink(@RequestParam String drinkName){

        System.out.println("Полученное имя напитка: " + drinkName);

        drinkName = decodeDrinkName(drinkName);

        System.out.println("Декодированное имя напитка: " + drinkName);
        return  ResponseEntity.ok(coffeeMachineService.makeDrink(drinkName));
    }

    @GetMapping("/popular")
    @Operation(summary = "Популярный напиток",
            description = "Возвращает часто заказываемый напиток")
    public ResponseEntity<String> getPopularDrink(){
        return  ResponseEntity.ok(coffeeMachineService.getPopularDrink());
    }

    @PutMapping("/newRecipe")
    @Operation(summary = "Добавить рецепт",
            description = "Добавляет новый рецепт")
    public ResponseEntity <List<Recipe>>addNewRecipe(@RequestParam String newDrinkName ,
                                               @RequestParam List<String> ingredients ,
                                                     @RequestParam List<Integer> amountsForRecipe ,
                                                @RequestParam List<Integer> newIngredientAmount){
        List<Recipe> recipes = coffeeMachineService.addNewRecipe(newDrinkName
                                                            , ingredients
                                                            ,amountsForRecipe
                                                            ,newIngredientAmount);

        return ResponseEntity.ok(recipes);
    }

    public String decodeDrinkName(String drinkName) {
        return URLDecoder.decode(drinkName, StandardCharsets.UTF_8);
    }

}
