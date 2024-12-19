package com.example.demo.exceptions;

public class NotEnoughIngredientException extends RuntimeException {
    public NotEnoughIngredientException(String message) {
        super(message);
    }
}
