package com.foodtable.express.restaurant.restaurant.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.restaurant.command.CreateRestaurantCommand;

@Component
public class CreateRestaurantCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateRestaurantCommandHandler.class);
    private final RestaurantRepository restaurantRepository;

    public CreateRestaurantCommandHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public RestaurantResponse handle(CreateRestaurantCommand command) {
        log.info("Handling CreateRestaurantCommand for name: {}", command.name());

        Restaurant restaurant = Restaurant.createRestaurant(
                command.name(),
                command.address(),
                command.openingTime(),
                command.closingTime());

        Restaurant saved = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromEntity(saved);
    }
}
