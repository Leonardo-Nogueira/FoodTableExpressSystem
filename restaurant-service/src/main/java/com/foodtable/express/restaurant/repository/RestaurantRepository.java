package com.foodtable.express.restaurant.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByNameContainingIgnoreCaseAndStatus(String name, RestaurantStatus status, Pageable pageable);
}
