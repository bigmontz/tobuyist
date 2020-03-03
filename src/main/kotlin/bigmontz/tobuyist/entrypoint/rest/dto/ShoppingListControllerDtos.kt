package bigmontz.tobuyist.entrypoint.rest.dto

import bigmontz.tobuyist.business.entity.Item
import bigmontz.tobuyist.business.entity.ShoppingList
import java.util.*

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

fun Item.toDto(): ItemDto {
    return ItemDto(
            itemId.value,
            product.value,
            quantity,
            unitOfMeasure.name,
            state.name)
}