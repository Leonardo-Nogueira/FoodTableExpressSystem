package com.foodtable.express.restaurant.restaurant.delegate;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.foodtable.express.restaurant.dto.RestaurantRequest;
import com.foodtable.express.restaurant.dto.RestaurantResponse;
import com.foodtable.express.restaurant.restaurant.command.CreateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.DeactivateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.UpdateRestaurantCommand;
import com.foodtable.express.restaurant.restaurant.command.handler.CreateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.command.handler.DeactivateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.command.handler.UpdateRestaurantCommandHandler;
import com.foodtable.express.restaurant.restaurant.query.GetActiveRestaurantsQuery;
import com.foodtable.express.restaurant.restaurant.query.GetRestaurantByIdQuery;
import com.foodtable.express.restaurant.restaurant.query.handler.GetActiveRestaurantsQueryHandler;
import com.foodtable.express.restaurant.restaurant.query.handler.GetRestaurantByIdQueryHandler;

@Component
public class RestaurantControllerDelegate {

    private final CreateRestaurantCommandHandler createHandler;
    private final UpdateRestaurantCommandHandler updateHandler;
    private final DeactivateRestaurantCommandHandler deactivateHandler;
    private final GetActiveRestaurantsQueryHandler listActiveHandler;
    private final GetRestaurantByIdQueryHandler getByIdHandler;

    public RestaurantControllerDelegate(
            CreateRestaurantCommandHandler createHandler,
            UpdateRestaurantCommandHandler updateHandler,
            DeactivateRestaurantCommandHandler deactivateHandler,
            GetActiveRestaurantsQueryHandler listActiveHandler,
            GetRestaurantByIdQueryHandler getByIdHandler) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.deactivateHandler = deactivateHandler;
        this.listActiveHandler = listActiveHandler;
        this.getByIdHandler = getByIdHandler;
    }

    public RestaurantResponse create(RestaurantRequest request) {
        var command = new CreateRestaurantCommand(
                request.name(),
                request.address(),
                request.openingTime(),
                request.closingTime());

        return createHandler.handle(command);
    }

    public Page<RestaurantResponse> listActive(String nameFilter, Pageable pageable) {
        var query = new GetActiveRestaurantsQuery(nameFilter, pageable);
        return listActiveHandler.handle(query);
    }

    public RestaurantResponse getById(UUID id) {
        var query = new GetRestaurantByIdQuery(id);
        return getByIdHandler.handle(query);
    }

    public RestaurantResponse update(UUID id, RestaurantRequest request) {
        var command = new UpdateRestaurantCommand(
                id,
                request.name(),
                request.address(),
                request.openingTime(),
                request.closingTime());

        return updateHandler.handle(command);
    }

    public void delete(UUID id) {
        var command = new DeactivateRestaurantCommand(id);
        deactivateHandler.handle(command);
    }
}
