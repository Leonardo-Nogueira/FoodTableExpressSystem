package com.foodtable.express.restaurant.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.foodtable.express.restaurant.event.RestaurantDeactivatedEvent;
import com.foodtable.express.restaurant.exception.InactiveRestaurantException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rs_restaurants")
public class Restaurant extends AbstractAggregateRoot<Restaurant> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RestaurantStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Restaurant() {
    }

    private Restaurant(String name, String address, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.status = RestaurantStatus.ACTIVE;
    }

    public static Restaurant createRestaurant(String name, String address, LocalTime openingTime,
            LocalTime closingTime) {
        return new Restaurant(name, address, openingTime, closingTime);
    }

    public static Restaurant createRestaurant(String name, String address, LocalTime openingTime,
            LocalTime closingTime, RestaurantStatus status) {
        Restaurant restaurant = new Restaurant(name, address, openingTime, closingTime);
        restaurant.status = status;
        return restaurant;
    }

    public void updateDetails(String name, String address, LocalTime openingTime, LocalTime closingTime) {
        if (isInactive()) {
            throw new InactiveRestaurantException("Cannot update an inactive restaurant");
        }
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public void deactivate() {
        if (isInactive()) {
            return;
        }
        this.status = RestaurantStatus.INACTIVE;

        registerEvent(new RestaurantDeactivatedEvent(this.id));
    }

    public boolean isActive() {
        return this.status == RestaurantStatus.ACTIVE;
    }

    public boolean isInactive() {
        return this.status == RestaurantStatus.INACTIVE;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    // Needed for tests or manual id assignment
    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
