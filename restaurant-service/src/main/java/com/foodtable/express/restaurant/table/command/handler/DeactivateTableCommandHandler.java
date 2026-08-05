package com.foodtable.express.restaurant.table.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;
import com.foodtable.express.restaurant.table.command.DeactivateTableCommand;

@Component
public class DeactivateTableCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(DeactivateTableCommandHandler.class);

    private final RestaurantTableRepository tableRepository;

    public DeactivateTableCommandHandler(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Transactional
    public void handle(DeactivateTableCommand command) {
        log.info("Handling DeactivateTableCommand for table: {} in restaurant: {}", command.tableId(), command.restaurantId());

        RestaurantTable table = tableRepository.findById(command.tableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        if (table.isInactive()) {
            log.info("Table with id: {} is already inactive", command.tableId());
            return;
        }

        table.deactivate(command.restaurantId());
        tableRepository.save(table);
    }
}
