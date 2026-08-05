package com.foodtable.express.restaurant.menuitem.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.menuitem.command.UpdateMenuItemCommand;

@Component
public class UpdateMenuItemCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateMenuItemCommandHandler.class);

    private final MenuItemRepository menuItemRepository;

    public UpdateMenuItemCommandHandler(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public MenuItemResponse handle(UpdateMenuItemCommand command) {
        log.info("Handling UpdateMenuItemCommand for item: {} in restaurant: {}", command.itemId(), command.restaurantId());

        MenuItem item = menuItemRepository.findById(command.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

        // Validações internas em MenuItem
        item.updateDetails(command.restaurantId(), command.name(), command.description(), command.price());

        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(updated);
    }
}
