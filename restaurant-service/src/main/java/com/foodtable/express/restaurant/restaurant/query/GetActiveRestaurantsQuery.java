package com.foodtable.express.restaurant.restaurant.query;

import org.springframework.data.domain.Pageable;

public record GetActiveRestaurantsQuery(
    String nameFilter,
    Pageable pageable
) {}
