package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.*
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.business.usecase.exception.AlreadyExistsException
import java.util.*
import java.util.concurrent.CompletionStage

class AddItemUseCase(
        private val getShoppingList: GetShoppingList,
        private val storeShoppingList: StoreShoppingList
) : UseCase<AddItemUseCase.Input, AddItemUseCase.Output> {

    override fun apply(input: Input): CompletionStage<Output> {
        val itemId = ItemId(input.itemId)
        var name = Name(input.shoppingListName)
        return getShoppingList.apply(GetShoppingList.Input(name))
                .thenApply { it.shoppingList ?: ShoppingList(name = name) }
                .thenApply { addItem(it, itemId, input) }
                .thenCompose {
                    storeShoppingList.apply(StoreShoppingList.Input(it))
                        .thenApply { output -> Output(output.name.value, it.item(itemId)!!) }
                }
    }

    private fun addItem(it: ShoppingList, itemId: ItemId, input: Input): ShoppingList {
        return when (it.item(itemId)) {
            null -> it.addItem(Item(
                    position = it.size,
                    itemId = itemId,
                    product = Product(input.product),
                    quantity = input.quantity,
                    unitOfMeasure = input.unitOfMeasure))
            else -> throw AlreadyExistsException("Item ${itemId.value} already exists")
        }
    }

    data class Input(
            val itemId: UUID,
            val shoppingListName: String,
            val product: String,
            val quantity: Int,
            val unitOfMeasure: UnitOfMeasure)

    data class Output(
            val shoppingListName: String,
            val item: Item
    )

}