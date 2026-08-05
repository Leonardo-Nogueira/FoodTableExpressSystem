package com.foodtable.express.restaurant.exception;

public class InactiveRestaurantException extends BusinessException {
    public InactiveRestaurantException(String message) {
        super(message);
    }
}
