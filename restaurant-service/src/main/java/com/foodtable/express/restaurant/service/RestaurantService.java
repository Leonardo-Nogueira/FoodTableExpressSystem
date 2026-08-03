package com.foodtable.express.restaurant.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodtable.express.restaurant.dto.RestaurantRequest;
import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;

@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             RestaurantTableRepository tableRepository,
                             MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public RestaurantResponse create(RestaurantRequest request) {
        log.info("Creating a new restaurant with name: {}", request.name());
        Restaurant restaurant = new Restaurant(
            request.name(),
            request.address(),
            request.openingTime(),
            request.closingTime(),
            RestaurantStatus.ACTIVE
        );
        Restaurant saved = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantResponse> listActive(String nameFilter, Pageable pageable) {
        log.info("Listing active restaurants with filter: {}", nameFilter);
        Page<Restaurant> page;
        if (nameFilter != null && !nameFilter.isBlank()) {
            page = restaurantRepository.findByNameContainingIgnoreCaseAndStatus(nameFilter, RestaurantStatus.ACTIVE, pageable);
        } else {
            page = restaurantRepository.findByStatus(RestaurantStatus.ACTIVE, pageable);
        }
        return page.map(RestaurantResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getById(UUID id) {
        log.info("Getting restaurant details for id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        
        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant is inactive");
        }
        return RestaurantResponse.fromEntity(restaurant);
    }

    @Transactional
    public RestaurantResponse update(UUID id, RestaurantRequest request) {
        log.info("Updating restaurant details for id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update an inactive restaurant");
        }

        restaurant.setName(request.name());
        restaurant.setAddress(request.address());
        restaurant.setOpeningTime(request.openingTime());
        restaurant.setClosingTime(request.closingTime());

        Restaurant updated = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deactivating (soft-deleting) restaurant with id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            log.info("Restaurant with id: {} is already inactive", id);
            return;
        }

        // Soft delete restaurant
        restaurant.setStatus(RestaurantStatus.INACTIVE);
        restaurantRepository.save(restaurant);

        // Soft delete all tables
        var tables = tableRepository.findByRestaurantIdAndStatus(id, TableStatus.ACTIVE);
        for (var table : tables) {
            table.setStatus(TableStatus.INACTIVE);
            tableRepository.save(table);
        }

        // Soft delete all menu items
        var menuItems = menuItemRepository.findByRestaurantIdAndStatus(id, MenuItemStatus.AVAILABLE);
        for (var item : menuItems) {
            item.setStatus(MenuItemStatus.UNAVAILABLE);
            menuItemRepository.save(item);
        }

        log.info("Restaurant, tables, and menu items soft-deleted successfully for restaurant id: {}", id);
    }
}
