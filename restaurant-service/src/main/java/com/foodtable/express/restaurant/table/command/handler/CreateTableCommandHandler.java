package com.foodtable.express.restaurant.table.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;
import com.foodtable.express.restaurant.table.command.CreateTableCommand;

@Component
public class CreateTableCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateTableCommandHandler.class);
    
    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;

    public CreateTableCommandHandler(RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public RestaurantTableResponse handle(CreateTableCommand command) {
        log.info("Handling CreateTableCommand for restaurant: {}", command.restaurantId());

        Restaurant restaurant = restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // Regras de negócio como verificação de se o restaurante está inativo ocorrem no construtor de RestaurantTable
        RestaurantTable table = RestaurantTable.createRestaurantTable(
                restaurant,
                command.label(),
                command.capacity());

        RestaurantTable saved = tableRepository.save(table);
        return RestaurantTableResponse.fromEntity(saved);
    }
}
