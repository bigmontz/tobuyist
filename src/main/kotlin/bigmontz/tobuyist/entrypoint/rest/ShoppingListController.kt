package bigmontz.tobuyist.entrypoint.rest

import bigmontz.tobuyist.business.entity.State
import bigmontz.tobuyist.business.entity.UnitOfMeasure
import bigmontz.tobuyist.business.usecase.AddItemUseCase
import bigmontz.tobuyist.business.usecase.DeleteItemUseCase
import bigmontz.tobuyist.business.usecase.GetShoppingListUseCase
import bigmontz.tobuyist.business.usecase.SetItemStateUseCase
import bigmontz.tobuyist.entrypoint.rest.dto.*
import java.util.*
import java.util.concurrent.CompletionStage
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path(value = "/shopping-list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ShoppingListController(
        private val getShoppingListUseCase: GetShoppingListUseCase,
        private val addItemUseCase: AddItemUseCase,
        private val setItemStateUseCase: SetItemStateUseCase,
        private val deleteItemUseCase: DeleteItemUseCase
) {

    @GET
    @Path(value = "/{name}")
    fun get(@PathParam(value = "name") name: String) : CompletionStage<ShoppingListDto> =
            getShoppingListUseCase.apply(GetShoppingListUseCase.Input(name))
                    .thenApply { it.shoppingList.toDto() }

    @POST
    @Path(value = "/{name}/item/{itemId}")
    fun post(
            @PathParam(value = "name") name: String,
            @PathParam(value = "itemId") itemId: UUID,
            createItemRequest: CreateItemRequest) : CompletionStage<ItemDto> =
                addItemUseCase.apply(AddItemUseCase.Input(
                        shoppingListName = name,
                        itemId = itemId,
                        unitOfMeasure = UnitOfMeasure.valueOf(createItemRequest.unitOfMeasure),
                        product = createItemRequest.product,
                        quantity = createItemRequest.quantity)
                )
                        .thenApply { it.item.toDto()  }

    @PATCH
    @Path(value = "/{name}/item/{itemId}")
    fun patch(
            @PathParam(value = "name") name: String,
            @PathParam(value = "itemId") itemId: UUID,
            map: Map<String, String>) : CompletionStage<ItemDto> =
                setItemStateUseCase.apply(SetItemStateUseCase.Input(
                        shoppingListName = name,
                        itemId = itemId,
                        state = State.valueOf(map["state"]!!))
                )
                        .thenApply { it.item.toDto() }

    @DELETE
    @Path(value = "/{name}/item/{itemId}")
    fun delete(
            @PathParam(value = "name") name: String,
            @PathParam(value = "itemId") itemId: UUID) : CompletionStage<Map<String, Any>> =
                deleteItemUseCase.apply(DeleteItemUseCase.Input(name, itemId))
                        .thenApply { mapOf(Pair("uuid", it.itemId)) }

}