package com.foodtable.express.restaurant.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address,

    @NotNull(message = "Opening time is required")
    LocalTime openingTime,

    @NotNull(message = "Closing time is required")
    LocalTime closingTime
) {}
