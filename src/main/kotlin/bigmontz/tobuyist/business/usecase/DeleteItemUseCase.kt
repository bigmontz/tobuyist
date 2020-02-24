package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.ItemId
import bigmontz.tobuyist.business.entity.Name
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.business.usecase.exception.ResourceNotFound
import java.util.*
import java.util.concurrent.CompletionStage

class DeleteItemUseCase(
        private val getShoppingList: GetShoppingList,
        private val storeShoppingList: StoreShoppingList
): UseCase<DeleteItemUseCase.Input, DeleteItemUseCase.Output>{

    override fun apply(input: Input): CompletionStage<Output> {
        val name = Name(input.shoppingListName)
        val itemId = ItemId(input.itemId)
        return getShoppingList.apply(GetShoppingList.Input(name))
                .thenApply { it.shoppingList ?: throw ResourceNotFound("Shopping list ${name.value} not found") }
                .thenApply { it.remove(itemId) }
                .thenCompose { storeShoppingList.apply(StoreShoppingList.Input(it)) }
                .thenApply { Output(itemId.value) }
    }

    data class Input(
            val shoppingListName: String,
            val itemId : UUID)

    data class Output(
            val itemId: UUID)


}