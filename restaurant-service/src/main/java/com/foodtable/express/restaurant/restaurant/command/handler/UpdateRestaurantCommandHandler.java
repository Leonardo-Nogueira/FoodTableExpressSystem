package com.foodtable.express.restaurant.restaurant.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.restaurant.command.UpdateRestaurantCommand;

@Component
public class UpdateRestaurantCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateRestaurantCommandHandler.class);
    private final RestaurantRepository restaurantRepository;

    public UpdateRestaurantCommandHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public RestaurantResponse handle(UpdateRestaurantCommand command) {
        log.info("Handling UpdateRestaurantCommand for id: {}", command.id());

        Restaurant restaurant = restaurantRepository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        restaurant.updateDetails(
                command.name(),
                command.address(),
                command.openingTime(),
                command.closingTime());

        Restaurant updated = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromEntity(updated);
    }
}
