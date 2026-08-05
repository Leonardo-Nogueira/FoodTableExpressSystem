package com.foodtable.express.restaurant.event;

import java.util.UUID;

public record RestaurantDeactivatedEvent(UUID restaurantId) {}
