package com.foodtable.express.restaurant.restaurant.command;

import java.time.LocalTime;

public record CreateRestaurantCommand(
    String name,
    String address,
    LocalTime openingTime,
    LocalTime closingTime
) {}
