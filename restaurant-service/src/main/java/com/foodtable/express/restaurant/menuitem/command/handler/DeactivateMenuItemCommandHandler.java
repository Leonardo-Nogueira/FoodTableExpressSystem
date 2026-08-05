package com.foodtable.express.restaurant.menuitem.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.menuitem.command.DeactivateMenuItemCommand;

@Component
public class DeactivateMenuItemCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeactivateMenuItemCommandHandler.class);

    private final MenuItemRepository menuItemRepository;

    public DeactivateMenuItemCommandHandler(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public void handle(DeactivateMenuItemCommand command) {
        log.info("Handling DeactivateMenuItemCommand for item: {} in restaurant: {}", command.itemId(), command.restaurantId());

        MenuItem item = menuItemRepository.findById(command.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

        if (item.isUnavailable()) {
            log.info("Menu item with id: {} is already unavailable", command.itemId());
            return;
        }

        item.deactivate(command.restaurantId());
        menuItemRepository.save(item);
    }
}
