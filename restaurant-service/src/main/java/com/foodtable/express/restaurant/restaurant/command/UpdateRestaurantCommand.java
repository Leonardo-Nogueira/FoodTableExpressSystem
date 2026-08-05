package com.foodtable.express.restaurant.restaurant.command;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateRestaurantCommand(
    UUID id,
    String name,
    String address,
    LocalTime openingTime,
    LocalTime closingTime
) {}
