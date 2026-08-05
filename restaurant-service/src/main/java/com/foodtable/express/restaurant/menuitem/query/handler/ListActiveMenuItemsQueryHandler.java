package com.foodtable.express.restaurant.menuitem.query.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.menuitem.query.ListActiveMenuItemsQuery;

@Component
public class ListActiveMenuItemsQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(ListActiveMenuItemsQueryHandler.class);

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public ListActiveMenuItemsQueryHandler(MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> handle(ListActiveMenuItemsQuery query) {
        log.info("Handling ListActiveMenuItemsQuery for restaurant id: {}", query.restaurantId());

        Restaurant restaurant = restaurantRepository.findById(query.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (restaurant.isInactive()) {
            throw new InactiveRestaurantException("Restaurant is inactive");
        }

        List<MenuItem> items = menuItemRepository.findByRestaurantIdAndStatus(query.restaurantId(), MenuItemStatus.AVAILABLE);
        return items.stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
