package com.foodtable.express.restaurant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.model.TableStatus;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    List<RestaurantTable> findByRestaurantIdAndStatus(UUID restaurantId, TableStatus status);
}
