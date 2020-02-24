package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.*
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.business.usecase.exception.ResourceNotFound
import java.util.*
import java.util.concurrent.CompletionStage

class SetItemStateUseCase (
        private val getShoppingList: GetShoppingList,
        private val storeShoppingList: StoreShoppingList
) : UseCase<SetItemStateUseCase.Input, SetItemStateUseCase.Output> {

    override fun apply(input: Input): CompletionStage<Output> {
        val name = Name(input.shoppingListName)
        val itemId = ItemId(input.itemId)
        return getShoppingList.apply(GetShoppingList.Input(name))
                .thenApply { it.shoppingList ?: ShoppingList(name = name) }
                .thenApply { it.editItem(itemId = itemId) { item -> item.copy(state = input.state) }}
                .thenCompose {
                    storeShoppingList.apply(StoreShoppingList.Input(it)).thenApply {
                        _ -> Output(it.item(itemId)?: throw ResourceNotFound("Item ${input.itemId} not found"))
                    }
                }
    }

    data class Input(
            val shoppingListName: String,
            val itemId: UUID,
            val state: State)

    data class Output(
            val item : Item)

}