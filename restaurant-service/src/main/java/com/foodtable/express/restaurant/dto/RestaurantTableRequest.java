package com.foodtable.express.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RestaurantTableRequest(
    @NotBlank(message = "Label is required")
    @Size(max = 50, message = "Label must not exceed 50 characters")
    String label,

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    Integer capacity
) {}
