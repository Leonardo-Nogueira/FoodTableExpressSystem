package com.foodtable.express.restaurant.table.command;

import java.util.UUID;

public record DeactivateTableCommand(
    UUID restaurantId,
    UUID tableId
) {}
