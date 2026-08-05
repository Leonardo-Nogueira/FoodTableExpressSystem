package com.foodtable.express.restaurant.model;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.foodtable.express.restaurant.exception.BusinessException;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rs_restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status;

    // Constructors
    public RestaurantTable() {
    }

    private RestaurantTable(Restaurant restaurant, String label, Integer capacity) {
        if (restaurant == null) {
            throw new BusinessException("Restaurant cannot be null");
        }
        if (restaurant.isInactive()) {
            throw new InactiveRestaurantException("Cannot add a table to an inactive restaurant");
        }
        this.restaurant = restaurant;
        this.label = label;
        this.capacity = capacity;
        this.status = TableStatus.ACTIVE;
    }

    public static RestaurantTable createRestaurantTable(Restaurant restaurant, String label, Integer capacity) {
        return new RestaurantTable(restaurant, label, capacity);
    }

    // Business Methods
    public void updateDetails(UUID restaurantId, String label, Integer capacity) {
        if (!this.restaurant.getId().equals(restaurantId)) {
            throw new BusinessException("Table does not belong to this restaurant");
        }

        if (isInactive() || this.restaurant.isInactive()) {
            throw new BusinessException("Cannot update an inactive table or table of an inactive restaurant");
        }

        this.label = label;
        this.capacity = capacity;
    }

    public void deactivate(UUID restaurantId) {
        if (!this.restaurant.getId().equals(restaurantId)) {
            throw new BusinessException("Table does not belong to this restaurant");
        }

        this.status = TableStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == TableStatus.ACTIVE;
    }

    public boolean isInactive() {
        return this.status == TableStatus.INACTIVE;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getLabel() {
        return label;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    // Setter for status needed for the Deactivation Listener or test mappings
    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
