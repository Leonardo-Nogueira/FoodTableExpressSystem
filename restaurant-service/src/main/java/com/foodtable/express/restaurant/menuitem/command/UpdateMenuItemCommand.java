package com.foodtable.express.restaurant.menuitem.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemCommand(
    UUID restaurantId,
    UUID itemId,
    String name,
    String description,
    BigDecimal price
) {}
