package com.foodtable.express.restaurant.table.query.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;
import com.foodtable.express.restaurant.table.query.ListActiveTablesQuery;

@Component
public class ListActiveTablesQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(ListActiveTablesQueryHandler.class);

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public ListActiveTablesQueryHandler(RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<RestaurantTableResponse> handle(ListActiveTablesQuery query) {
        log.info("Handling ListActiveTablesQuery for restaurant id: {}", query.restaurantId());

        Restaurant restaurant = restaurantRepository.findById(query.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (restaurant.isInactive()) {
            throw new InactiveRestaurantException("Restaurant is inactive");
        }

        List<RestaurantTable> tables = tableRepository.findByRestaurantIdAndStatus(query.restaurantId(), TableStatus.ACTIVE);

        return tables.stream()
                .map(RestaurantTableResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
