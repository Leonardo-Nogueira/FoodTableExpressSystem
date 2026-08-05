package com.foodtable.express.restaurant.table.command;

import java.util.UUID;

public record UpdateTableCommand(
    UUID restaurantId,
    UUID tableId,
    String label,
    Integer capacity
) {}
