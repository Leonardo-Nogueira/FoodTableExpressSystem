package com.foodtable.express.restaurant.restaurant.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.restaurant.command.DeactivateRestaurantCommand;

@Component
public class DeactivateRestaurantCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeactivateRestaurantCommandHandler.class);
    private final RestaurantRepository restaurantRepository;

    public DeactivateRestaurantCommandHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public void handle(DeactivateRestaurantCommand command) {
        log.info("Handling DeactivateRestaurantCommand for id: {}", command.id());

        Restaurant restaurant = restaurantRepository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        restaurant.deactivate();
        restaurantRepository.save(restaurant);
    }
}
