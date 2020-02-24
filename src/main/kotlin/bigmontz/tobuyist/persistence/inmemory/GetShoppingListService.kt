package bigmontz.tobuyist.persistence.inmemory

import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class GetShoppingListService : GetShoppingList {
    override fun apply(input: GetShoppingList.Input): CompletionStage<GetShoppingList.Output> {
        print("Getting shopping list ${input.name.value} ")
        println(ShoppingListStorage[input.name])
        return CompletableFuture.supplyAsync {
            GetShoppingList.Output(ShoppingListStorage[input.name])
        }
    }
}