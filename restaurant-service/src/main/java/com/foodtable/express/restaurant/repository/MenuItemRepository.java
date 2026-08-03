package com.foodtable.express.restaurant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.model.MenuItemStatus;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantIdAndStatus(UUID restaurantId, MenuItemStatus status);
}
