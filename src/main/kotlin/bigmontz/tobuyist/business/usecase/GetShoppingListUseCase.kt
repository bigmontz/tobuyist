package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.Name
import bigmontz.tobuyist.business.entity.ShoppingList
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList

import java.util.concurrent.CompletionStage
import java.util.logging.Logger


class GetShoppingListUseCase(
        private val checkShoppingListExists: GetShoppingList
) : UseCase<GetShoppingListUseCase.Input, GetShoppingListUseCase.Output> {

    override fun apply(input: Input): CompletionStage<Output> {
        logger.info("Starting UseCase for input=${input}")
        val name = Name(input.name)
        return checkShoppingListExists.apply(GetShoppingList.Input(name))
                .thenApply { it.shoppingList ?: ShoppingList(name)}
                .thenApply { Output(it) }
    }

    companion object {
        val logger: Logger = Logger.getLogger("GetShoppingListUseCase")
    }


    data class Input (val name: String)
    data class Output (val shoppingList: ShoppingList)
}