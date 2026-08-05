package com.foodtable.express.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.event.RestaurantDeactivatedEvent;
import com.foodtable.express.restaurant.event.listener.RestaurantDeactivationListener;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;
import com.foodtable.express.restaurant.model.MenuItem;
import com.foodtable.express.restaurant.model.MenuItemStatus;
import com.foodtable.express.restaurant.model.Restaurant;
import com.foodtable.express.restaurant.model.RestaurantStatus;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.model.TableStatus;
import com.foodtable.express.restaurant.repository.MenuItemRepository;
import com.foodtable.express.restaurant.repository.RestaurantRepository;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;
import com.foodtable.express.restaurant.restaurant.command.CreateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.DeactivateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.UpdateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.handler.CreateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.command.handler.DeactivateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.command.handler.UpdateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.query.GetRestaurantByIdQuery;
import com.foodtable.express.restaurant.restaurant.query.handler.GetRestaurantByIdQueryHandler;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private CreateRestaurantCommandHandler createHandler;

    @InjectMocks
    private UpdateRestaurantCommandHandler updateHandler;

    @InjectMocks
    private DeactivateRestaurantCommandHandler deactivateHandler;

    @InjectMocks
    private GetRestaurantByIdQueryHandler getByIdHandler;

    @InjectMocks
    private RestaurantDeactivationListener deactivationListener;

    @Test
    public void create_ShouldSaveAndReturnResponse() {
        // Arrange
        CreateRestaurantCommand command = new CreateRestaurantCommand("Italian Bistro", "123 Main St", LocalTime.of(18, 0), LocalTime.of(23, 0));
        Restaurant restaurant = Restaurant.createRestaurant("Italian Bistro", "123 Main St", LocalTime.of(18, 0), LocalTime.of(23, 0), RestaurantStatus.ACTIVE);
        restaurant.setId(UUID.randomUUID());

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        RestaurantResponse response = createHandler.handle(command);

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
        Restaurant restaurant = Restaurant.createRestaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.INACTIVE);
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        assertThrows(InactiveRestaurantException.class, () -> getByIdHandler.handle(new GetRestaurantByIdQuery(id)));
        verify(restaurantRepository).findById(id);
    }

    @Test
    public void getById_ShouldThrowNotFound_WhenRestaurantDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> getByIdHandler.handle(new GetRestaurantByIdQuery(id)));
        verify(restaurantRepository).findById(id);
    }

    @Test
    public void update_ShouldUpdateDetailsWhenActive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.createRestaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.ACTIVE);
        restaurant.setId(id);

        UpdateRestaurantCommand command = new UpdateRestaurantCommand(id, "Updated Bistro", "New Address", LocalTime.of(11, 0), LocalTime.of(22, 0));

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RestaurantResponse response = updateHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Bistro", response.name());
        assertEquals("New Address", response.address());
        assertEquals(LocalTime.of(11, 0), response.openingTime());
        assertEquals(LocalTime.of(22, 0), response.closingTime());

        verify(restaurantRepository).findById(id);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    public void update_ShouldThrowException_WhenRestaurantInactive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.createRestaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.INACTIVE);
        restaurant.setId(id);

        UpdateRestaurantCommand command = new UpdateRestaurantCommand(id, "Updated Bistro", "New Address", LocalTime.of(11, 0), LocalTime.of(22, 0));

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        assertThrows(InactiveRestaurantException.class, () -> updateHandler.handle(command));
        verify(restaurantRepository).findById(id);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    public void deactivate_ShouldChangeStatusAndSave() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.createRestaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.ACTIVE);
        restaurant.setId(id);

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // Act
        deactivateHandler.handle(new DeactivateRestaurantCommand(id));

        // Assert
        assertEquals(RestaurantStatus.INACTIVE, restaurant.getStatus());
        verify(restaurantRepository).findById(id);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    public void listener_ShouldDeactivateTablesAndMenuItems() {
        // Arrange
        UUID id = UUID.randomUUID();
        Restaurant restaurant = Restaurant.createRestaurant("Bistro", "Address", LocalTime.NOON, LocalTime.MIDNIGHT, RestaurantStatus.ACTIVE);
        restaurant.setId(id);

        RestaurantTable table = RestaurantTable.createRestaurantTable(restaurant, "Table 1", 4);
        MenuItem menuItem = MenuItem.createMenuItem(restaurant, "Pizza", "Delicious", java.math.BigDecimal.TEN);

        when(tableRepository.findByRestaurantIdAndStatus(id, TableStatus.ACTIVE)).thenReturn(List.of(table));
        when(menuItemRepository.findByRestaurantIdAndStatus(id, MenuItemStatus.AVAILABLE)).thenReturn(List.of(menuItem));

        // Act
        deactivationListener.handleRestaurantDeactivated(new RestaurantDeactivatedEvent(id));

        // Assert
        assertEquals(TableStatus.INACTIVE, table.getStatus());
        assertEquals(MenuItemStatus.UNAVAILABLE, menuItem.getStatus());

        verify(tableRepository).findByRestaurantIdAndStatus(id, TableStatus.ACTIVE);
        verify(tableRepository).save(table);
        verify(menuItemRepository).findByRestaurantIdAndStatus(id, MenuItemStatus.AVAILABLE);
        verify(menuItemRepository).save(menuItem);
    }
}
