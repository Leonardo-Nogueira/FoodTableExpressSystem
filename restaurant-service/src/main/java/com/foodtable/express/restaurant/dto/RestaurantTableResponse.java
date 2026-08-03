package com.foodtable.express.restaurant.dto;

import java.util.UUID;

import com.foodtable.express.restaurant.model.RestaurantTable;

public record RestaurantTableResponse(
    UUID id,
    UUID restaurantId,
    String label,
    Integer capacity,
    String status
) {
    public static RestaurantTableResponse fromEntity(RestaurantTable table) {
        return new RestaurantTableResponse(
            table.getId(),
            table.getRestaurant().getId(),
            table.getLabel(),
            table.getCapacity(),
            table.getStatus().name()
        );
    }
}
