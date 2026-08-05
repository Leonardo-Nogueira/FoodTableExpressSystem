package com.foodtable.express.restaurant.table.command;

import java.util.UUID;

public record CreateTableCommand(
    UUID restaurantId,
    String label,
    Integer capacity
) {}
