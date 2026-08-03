package com.foodtable.express.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.foodtable.express.restaurant.dto.RestaurantRequest;
import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void create_ShouldSaveAndReturnResponse() {
        // Arrange
        RestaurantRequest request = new RestaurantRequest("Italian Bistro", "123 Main St", LocalTime.of(18, 0), LocalTime.of(23, 0));
        Restaurant restaurant = new Restaurant("Italian Bistro", "123 Main St", LocalTime.of(18, 0), LocalTime.of(23, 0), RestaurantStatus.ACTIVE);
        restaurant.setId(UUID.randomUUID());

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        RestaurantResponse response = restaurantService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(restaurant.getId(), response.id());
        assertEquals("Italian Bistro", response.name());
        assertEquals("ACTIVE", response.status());

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    public void getById_ShouldThrowNotFound_WhenRestaurantInactive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = new Restaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.INACTIVE);
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> restaurantService.getById(id));
        verify(restaurantRepository).findById(id);
    }

    @Test
    public void delete_ShouldDeactivateRestaurantAndItsRelatedData() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = new Restaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.ACTIVE);
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(tableRepository.findByRestaurantIdAndStatus(id, TableStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(menuItemRepository.findByRestaurantIdAndStatus(id, MenuItemStatus.AVAILABLE)).thenReturn(Collections.emptyList());

        // Act
        restaurantService.delete(id);

        // Assert
        assertEquals(RestaurantStatus.INACTIVE, restaurant.getStatus());
        verify(restaurantRepository).findById(id);
        verify(restaurantRepository).save(restaurant);
        verify(tableRepository).findByRestaurantIdAndStatus(id, TableStatus.ACTIVE);
        verify(menuItemRepository).findByRestaurantIdAndStatus(id, MenuItemStatus.AVAILABLE);
    }
}
