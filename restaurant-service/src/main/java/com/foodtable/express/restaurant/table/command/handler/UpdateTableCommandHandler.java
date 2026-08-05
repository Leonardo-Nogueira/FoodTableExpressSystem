package com.foodtable.express.restaurant.table.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.exception.EntityNotFoundException;
import com.foodtable.express.restaurant.model.RestaurantTable;
import com.foodtable.express.restaurant.repository.RestaurantTableRepository;
import com.foodtable.express.restaurant.table.command.UpdateTableCommand;

@Component
public class UpdateTableCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateTableCommandHandler.class);

    private final RestaurantTableRepository tableRepository;

    public UpdateTableCommandHandler(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Transactional
    public RestaurantTableResponse handle(UpdateTableCommand command) {
        log.info("Handling UpdateTableCommand for table: {} in restaurant: {}", command.tableId(), command.restaurantId());

        RestaurantTable table = tableRepository.findById(command.tableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        // Validações encapsuladas no domínio de RestaurantTable
        table.updateDetails(command.restaurantId(), command.label(), command.capacity());

        RestaurantTable updated = tableRepository.save(table);
        return RestaurantTableResponse.fromEntity(updated);
    }
}
