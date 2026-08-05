package com.foodtable.express.restaurant.table.delegate;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.foodtable.express.restaurant.dto.RestaurantTableRequest;
import com.foodtable.express.restaurant.dto.RestaurantTableResponse;
import com.foodtable.express.restaurant.table.command.CreateTableCommand;
import com.foodtable.express.restaurant.table.command.DeactivateTableCommand;
import com.foodtable.express.restaurant.table.command.UpdateTableCommand;
import com.foodtable.express.restaurant.table.command.handler.CreateTableCommandHandler;
import com.foodtable.express.restaurant.table.command.handler.DeactivateTableCommandHandler;
import com.foodtable.express.restaurant.table.command.handler.UpdateTableCommandHandler;
import com.foodtable.express.restaurant.table.query.ListActiveTablesQuery;
import com.foodtable.express.restaurant.table.query.handler.ListActiveTablesQueryHandler;

@Component
public class TableControllerDelegate {

    private final CreateTableCommandHandler createHandler;
    private final UpdateTableCommandHandler updateHandler;
    private final DeactivateTableCommandHandler deactivateHandler;
    private final ListActiveTablesQueryHandler listActiveHandler;

    public TableControllerDelegate(
            CreateTableCommandHandler createHandler,
            UpdateTableCommandHandler updateHandler,
            DeactivateTableCommandHandler deactivateHandler,
            ListActiveTablesQueryHandler listActiveHandler) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deactivateHandler = deactivateHandler;
        this.listActiveHandler = listActiveHandler;
    }

    public RestaurantTableResponse create(UUID restaurantId, RestaurantTableRequest request) {
        var command = new CreateTableCommand(restaurantId, request.label(), request.capacity());
        return createHandler.handle(command);
    }

    public List<RestaurantTableResponse> listActive(UUID restaurantId) {
        var query = new ListActiveTablesQuery(restaurantId);
        return listActiveHandler.handle(query);
    }

    public RestaurantTableResponse update(UUID restaurantId, UUID tableId, RestaurantTableRequest request) {
        var command = new UpdateTableCommand(restaurantId, tableId, request.label(), request.capacity());
        return updateHandler.handle(command);
    }

    public void delete(UUID restaurantId, UUID tableId) {
        var command = new DeactivateTableCommand(restaurantId, tableId);
        deactivateHandler.handle(command);
    }
}
