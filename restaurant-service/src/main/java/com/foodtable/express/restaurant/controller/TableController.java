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

import com.foodtable.express.restaurant.dto.RestaurantTableRequest;
import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.service.RestaurantTableService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants/{id}/tables")
public class TableController {

    private final RestaurantTableService tableService;

    public TableController(RestaurantTableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    public ResponseEntity<RestaurantTableResponse> create(
            @PathVariable("id") UUID restaurantId,
            @Valid @RequestBody RestaurantTableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(restaurantId, request));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTableResponse>> listActive(
            @PathVariable("id") UUID restaurantId) {
        return ResponseEntity.ok(tableService.listActiveByRestaurant(restaurantId));
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<RestaurantTableResponse> update(
            @PathVariable("id") UUID restaurantId,
            @PathVariable("tableId") UUID tableId,
            @Valid @RequestBody RestaurantTableRequest request) {
        return ResponseEntity.ok(tableService.update(restaurantId, tableId, request));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID restaurantId,
            @PathVariable("tableId") UUID tableId) {
        tableService.delete(restaurantId, tableId);
        return ResponseEntity.noContent().build();
    }
}
