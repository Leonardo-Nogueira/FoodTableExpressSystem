package com.foodtable.express.restaurant.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodtable.express.restaurant.dto.RestaurantTableRequest;
import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;

@Service
public class RestaurantTableService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantTableService.class);

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantTableService(RestaurantTableRepository tableRepository,
                                  RestaurantRepository restaurantRepository) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public RestaurantTableResponse create(UUID restaurantId, RestaurantTableRequest request) {
        log.info("Creating a new table for restaurant: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add a table to an inactive restaurant");
        }

        RestaurantTable table = new RestaurantTable(
            restaurant,
            request.label(),
            request.capacity(),
            TableStatus.ACTIVE
        );
        RestaurantTable saved = tableRepository.save(table);
        return RestaurantTableResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RestaurantTableResponse> listActiveByRestaurant(UUID restaurantId) {
        log.info("Listing active tables for restaurant id: {}", restaurantId);
        // Verify restaurant exists and is active
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant is inactive");
        }

        List<RestaurantTable> tables = tableRepository.findByRestaurantIdAndStatus(restaurantId, TableStatus.ACTIVE);
        return tables.stream()
            .map(RestaurantTableResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantTableResponse update(UUID restaurantId, UUID tableId, RestaurantTableRequest request) {
        log.info("Updating table details for table: {} in restaurant: {}", tableId, restaurantId);
        RestaurantTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));

        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table does not belong to this restaurant");
        }

        if (table.getStatus() == TableStatus.INACTIVE || table.getRestaurant().getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update an inactive table or table of an inactive restaurant");
        }

        table.setLabel(request.label());
        table.setCapacity(request.capacity());

        RestaurantTable updated = tableRepository.save(table);
        return RestaurantTableResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(UUID restaurantId, UUID tableId) {
        log.info("Deactivating (soft-deleting) table with id: {} in restaurant: {}", tableId, restaurantId);
        RestaurantTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));

        if (!table.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table does not belong to this restaurant");
        }

        if (table.getStatus() == TableStatus.INACTIVE) {
            log.info("Table with id: {} is already inactive", tableId);
            return;
        }

        table.setStatus(TableStatus.INACTIVE);
        tableRepository.save(table);
        log.info("Table soft-deleted successfully");
    }
}
