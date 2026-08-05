package com.foodtable.express.restaurant.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.foodtable.express.restaurant.event.RestaurantDeactivatedEvent;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;

@Component
public class RestaurantDeactivationListener {

    private static final Logger log = LoggerFactory.getLogger(RestaurantDeactivationListener.class);

    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantDeactivationListener(RestaurantTableRepository tableRepository,
            MenuItemRepository menuItemRepository) {
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @EventListener
    public void handleRestaurantDeactivated(RestaurantDeactivatedEvent event) {
        log.info("Handling RestaurantDeactivatedEvent for restaurant id: {}", event.restaurantId());

        var tables = tableRepository.findByRestaurantIdAndStatus(event.restaurantId(), TableStatus.ACTIVE);

        for (var table : tables) {
            table.setStatus(TableStatus.INACTIVE);
            tableRepository.save(table);
        }

        log.info("Deactivated {} tables for restaurant id: {}", tables.size(), event.restaurantId());

        var menuItems = menuItemRepository.findByRestaurantIdAndStatus(event.restaurantId(), MenuItemStatus.AVAILABLE);

        for (var item : menuItems) {
            item.setStatus(MenuItemStatus.UNAVAILABLE);
            menuItemRepository.save(item);

        }
        log.info("Deactivated {} menu items for restaurant id: {}", menuItems.size(), event.restaurantId());
    }
}
