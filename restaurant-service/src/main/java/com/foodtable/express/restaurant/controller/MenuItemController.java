package com.foodtable.express.restaurant.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodtable.express.restaurant.dto.MenuItemRequest;
import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.service.MenuItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants/{id}/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@PathVariable("id") UUID restaurantId,
            @Valid @RequestBody MenuItemRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.create(restaurantId, request));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> listActive(@PathVariable("id") UUID restaurantId) {
        return ResponseEntity.ok(menuItemService.listActiveByRestaurant(restaurantId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItemResponse> update(@PathVariable("id") UUID restaurantId,
            @PathVariable("itemId") UUID itemId,
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.update(restaurantId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID restaurantId,
            @PathVariable("itemId") UUID itemId) {
        menuItemService.delete(restaurantId, itemId);
        return ResponseEntity.noContent().build();
    }
}
