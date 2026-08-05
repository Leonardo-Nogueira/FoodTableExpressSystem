package com.foodtable.express.restaurant.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodtable.express.restaurant.dto.RestaurantRequest;
import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.restaurant.delegate.RestaurantControllerDelegate;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantControllerDelegate delegate;

    public RestaurantController(RestaurantControllerDelegate delegate) {
        this.delegate = delegate;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(delegate.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> listActive(
            @RequestParam(value = "name", required = false) String nameFilter,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(delegate.listActive(nameFilter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(delegate.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RestaurantRequest request) {

        return ResponseEntity.ok(delegate.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        delegate.delete(id);
        return ResponseEntity.noContent().build();
    }
}
