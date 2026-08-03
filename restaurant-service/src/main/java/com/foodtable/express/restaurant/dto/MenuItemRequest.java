package com.foodtable.express.restaurant.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuItemRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    BigDecimal price
) {}
