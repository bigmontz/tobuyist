package bigmontz.tobuyist.persistence.inmemory

import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class StoreShoppingListService : StoreShoppingList {
    override fun apply(input: StoreShoppingList.Input): CompletionStage<StoreShoppingList.Output> {
        ShoppingListStorage[input.shoppingList.name] = input.shoppingList
        println(ShoppingListStorage)
        return CompletableFuture.supplyAsync {
            StoreShoppingList.Output(input.shoppingList.name)
        }
    }
}