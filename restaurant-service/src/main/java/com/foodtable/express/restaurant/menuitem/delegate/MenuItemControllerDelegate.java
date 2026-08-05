package com.foodtable.express.restaurant.menuitem.delegate;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.foodtable.express.restaurant.dto.MenuItemRequest;
import com.foodtable.express.restaurant.dto.MenuItemResponse;
import com.foodtable.express.restaurant.menuitem.command.CreateMenuItemCommand;
import com.foodtable.express.restaurant.menuitem.command.DeactivateMenuItemCommand;
import com.foodtable.express.restaurant.menuitem.command.UpdateMenuItemCommand;
import com.foodtable.express.restaurant.menuitem.command.handler.CreateMenuItemCommandHandler;
import com.foodtable.express.restaurant.menuitem.command.handler.DeactivateMenuItemCommandHandler;
import com.foodtable.express.restaurant.menuitem.command.handler.UpdateMenuItemCommandHandler;
import com.foodtable.express.restaurant.menuitem.query.ListActiveMenuItemsQuery;
import com.foodtable.express.restaurant.menuitem.query.handler.ListActiveMenuItemsQueryHandler;

@Component
public class MenuItemControllerDelegate {

    private final CreateMenuItemCommandHandler createHandler;
    private final UpdateMenuItemCommandHandler updateHandler;
    private final DeactivateMenuItemCommandHandler deactivateHandler;
    private final ListActiveMenuItemsQueryHandler listActiveHandler;

    public MenuItemControllerDelegate(
            CreateMenuItemCommandHandler createHandler,
            UpdateMenuItemCommandHandler updateHandler,
            DeactivateMenuItemCommandHandler deactivateHandler,
            ListActiveMenuItemsQueryHandler listActiveHandler) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deactivateHandler = deactivateHandler;
        this.listActiveHandler = listActiveHandler;
    }

    public MenuItemResponse create(UUID restaurantId, MenuItemRequest request) {
        var command = new CreateMenuItemCommand(restaurantId, request.name(), request.description(), request.price());
        return createHandler.handle(command);
    }

    public List<MenuItemResponse> listActive(UUID restaurantId) {
        var query = new ListActiveMenuItemsQuery(restaurantId);
        return listActiveHandler.handle(query);
    }

    public MenuItemResponse update(UUID restaurantId, UUID itemId, MenuItemRequest request) {
        var command = new UpdateMenuItemCommand(restaurantId, itemId, request.name(), request.description(), request.price());
        return updateHandler.handle(command);
    }

    public void delete(UUID restaurantId, UUID itemId) {
        var command = new DeactivateMenuItemCommand(restaurantId, itemId);
        deactivateHandler.handle(command);
    }
}
