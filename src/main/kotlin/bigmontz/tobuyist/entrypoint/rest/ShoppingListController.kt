package bigmontz.tobuyist.entrypoint.rest

import bigmontz.tobuyist.business.entity.Item
import bigmontz.tobuyist.business.entity.ShoppingList
import bigmontz.tobuyist.business.entity.State
import bigmontz.tobuyist.business.entity.UnitOfMeasure
import bigmontz.tobuyist.business.usecase.AddItemUseCase
import bigmontz.tobuyist.business.usecase.DeleteItemUseCase
import bigmontz.tobuyist.business.usecase.GetShoppingListUseCase
import bigmontz.tobuyist.business.usecase.SetItemStateUseCase
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

data class ItemDto  (
        val uuid: UUID,
        val product: String,
        val quantity : Int,
        val unitOfMeasure : String,
        val state : String)

data class ShoppingListDto(val name: String, val items: List<ItemDto>)

data class CreateItemRequest(
        val product: String,
        val quantity: Int,
        val unitOfMeasure: String)

data class PatchItemRequestDto(val state: String)

fun ShoppingList.toDto(): ShoppingListDto {
    val name : String = this.name.value
    val items : List<ItemDto> = map { it.toDto() }
    return ShoppingListDto(name, items)
}

private fun Item.toDto(): ItemDto {
    return ItemDto(
            itemId.value,
            product.value,
            quantity,
            unitOfMeasure.name,
            state.name)
}

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
            @PathParam(value = "itemId") itemId: UUID) : CompletionStage<UUID> =
                deleteItemUseCase.apply(DeleteItemUseCase.Input(name, itemId))
                        .thenApply { it.itemId }

}