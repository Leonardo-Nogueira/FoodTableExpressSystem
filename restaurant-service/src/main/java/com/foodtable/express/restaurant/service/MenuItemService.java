package com.foodtable.express.restaurant.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodtable.express.restaurant.dto.MenuItemRequest;
import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;

@Service
public class MenuItemService {

    private static final Logger log = LoggerFactory.getLogger(MenuItemService.class);

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public MenuItemResponse create(UUID restaurantId, MenuItemRequest request) {
        log.info("Creating a new menu item for restaurant: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add a menu item to an inactive restaurant");
        }

        MenuItem item = new MenuItem(
            restaurant,
            request.name(),
            request.description(),
            request.price(),
            MenuItemStatus.AVAILABLE
        );
        MenuItem saved = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> listActiveByRestaurant(UUID restaurantId) {
        log.info("Listing active menu items for restaurant id: {}", restaurantId);
        // Verify restaurant exists and is active
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        if (restaurant.getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant is inactive");
        }

        List<MenuItem> items = menuItemRepository.findByRestaurantIdAndStatus(restaurantId, MenuItemStatus.AVAILABLE);
        return items.stream()
            .map(MenuItemResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemResponse update(UUID restaurantId, UUID itemId, MenuItemRequest request) {
        log.info("Updating menu item details for item: {} in restaurant: {}", itemId, restaurantId);
        MenuItem item = menuItemRepository.findById(itemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item does not belong to this restaurant");
        }

        if (item.getStatus() == MenuItemStatus.UNAVAILABLE || item.getRestaurant().getStatus() == RestaurantStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update an unavailable menu item or item of an inactive restaurant");
        }

        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());

        MenuItem updated = menuItemRepository.save(item);
        return MenuItemResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(UUID restaurantId, UUID itemId) {
        log.info("Deactivating (soft-deleting) menu item with id: {} in restaurant: {}", itemId, restaurantId);
        MenuItem item = menuItemRepository.findById(itemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item does not belong to this restaurant");
        }

        if (item.getStatus() == MenuItemStatus.UNAVAILABLE) {
            log.info("Menu item with id: {} is already unavailable", itemId);
            return;
        }

        item.setStatus(MenuItemStatus.UNAVAILABLE);
        menuItemRepository.save(item);
        log.info("Menu item soft-deleted successfully");
    }
}
