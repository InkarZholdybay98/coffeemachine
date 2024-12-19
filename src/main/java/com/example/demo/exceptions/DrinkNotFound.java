package com.example.demo.exceptions;

public class DrinkNotFound extends RuntimeException {
    public DrinkNotFound(String message) {
        super(message);
    }
}
