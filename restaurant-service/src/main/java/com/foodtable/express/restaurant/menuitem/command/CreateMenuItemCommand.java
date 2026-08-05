package com.foodtable.express.restaurant.menuitem.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemCommand(
    UUID restaurantId,
    String name,
    String description,
    BigDecimal price
) {}
