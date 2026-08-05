package com.foodtable.express.restaurant.restaurant.query.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.restaurant.query.GetActiveRestaurantsQuery;

@Component
public class GetActiveRestaurantsQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetActiveRestaurantsQueryHandler.class);
    private final RestaurantRepository restaurantRepository;

    public GetActiveRestaurantsQueryHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public Page<RestaurantResponse> handle(GetActiveRestaurantsQuery query) {
        log.info("Handling GetActiveRestaurantsQuery with filter: {}", query.nameFilter());

        Page<Restaurant> page;

        if (query.nameFilter() != null && !query.nameFilter().isBlank()) {
            page = restaurantRepository.findByNameContainingIgnoreCaseAndStatus(
                    query.nameFilter(),
                    RestaurantStatus.ACTIVE,
                    query.pageable());
        } else {
            page = restaurantRepository.findByStatus(RestaurantStatus.ACTIVE, query.pageable());
        }

        return page.map(RestaurantResponse::fromEntity);
    }
}
