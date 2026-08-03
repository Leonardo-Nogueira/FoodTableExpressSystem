package com.foodtable.express.restaurant.dto;

import java.time.LocalTime;
import java.util.UUID;

import com.foodtable.express.restaurant.model.Restaurant;

public record RestaurantResponse(
    UUID id,
    String name,
    String address,
    LocalTime openingTime,
    LocalTime closingTime,
    String status
) {
    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getAddress(),
            restaurant.getOpeningTime(),
            restaurant.getClosingTime(),
            restaurant.getStatus().name()
        );
    }
}
