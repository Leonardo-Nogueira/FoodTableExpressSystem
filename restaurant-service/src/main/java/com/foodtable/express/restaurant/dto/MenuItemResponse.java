package com.foodtable.express.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.foodtable.express.restaurant.model.MenuItem;

public record MenuItemResponse(
    UUID id,
    UUID restaurantId,
    String name,
    String description,
    BigDecimal price,
    String status
) {
    public static MenuItemResponse fromEntity(MenuItem item) {
        return new MenuItemResponse(
            item.getId(),
            item.getRestaurant().getId(),
            item.getName(),
            item.getDescription(),
            item.getPrice(),
            item.getStatus().name()
        );
    }
}
