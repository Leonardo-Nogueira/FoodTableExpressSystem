package com.foodtable.express.restaurant.menuitem.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.menuitem.command.CreateMenuItemCommand;

@Component
public class CreateMenuItemCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateMenuItemCommandHandler.class);

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public CreateMenuItemCommandHandler(MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public MenuItemResponse handle(CreateMenuItemCommand command) {
        log.info("Handling CreateMenuItemCommand for restaurant: {}", command.restaurantId());

        Restaurant restaurant = restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // Validações internas do domínio em MenuItem construtor
        MenuItem menuItem = MenuItem.createMenuItem(
                restaurant,
                command.name(),
                command.description(),
                command.price());

        MenuItem saved = menuItemRepository.save(menuItem);
        return MenuItemResponse.fromEntity(saved);
    }
}
