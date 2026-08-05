package com.foodtable.express.restaurant.restaurant.query.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.restaurant.query.GetRestaurantByIdQuery;

@Component
public class GetRestaurantByIdQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetRestaurantByIdQueryHandler.class);
    private final RestaurantRepository restaurantRepository;

    public GetRestaurantByIdQueryHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public RestaurantResponse handle(GetRestaurantByIdQuery query) {
        log.info("Handling GetRestaurantByIdQuery for id: {}", query.id());

        Restaurant restaurant = restaurantRepository.findById(query.id())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (restaurant.isInactive()) {
            throw new InactiveRestaurantException("Restaurant is inactive");
        }

        return RestaurantResponse.fromEntity(restaurant);
    }
}
