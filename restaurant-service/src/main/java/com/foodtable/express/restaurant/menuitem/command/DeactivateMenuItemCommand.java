package com.foodtable.express.restaurant.menuitem.command;

import java.util.UUID;

public record DeactivateMenuItemCommand(
    UUID restaurantId,
    UUID itemId
) {}
