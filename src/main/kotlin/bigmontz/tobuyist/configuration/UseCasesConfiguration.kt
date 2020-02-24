package bigmontz.tobuyist.configuration

import bigmontz.tobuyist.business.usecase.AddItemUseCase
import bigmontz.tobuyist.business.usecase.DeleteItemUseCase
import bigmontz.tobuyist.business.usecase.GetShoppingListUseCase
import bigmontz.tobuyist.business.usecase.SetItemStateUseCase
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces

@Dependent
class UseCasesConfiguration {

    @Produces
    fun shoppingListUseCase(
            getShoppingList: GetShoppingList
    ) : GetShoppingListUseCase = GetShoppingListUseCase(getShoppingList)

    @Produces
    fun addItemToShoppingListUseCase(
            getShoppingList: GetShoppingList,
            storeShoppingList: StoreShoppingList
    ) : AddItemUseCase = AddItemUseCase(getShoppingList, storeShoppingList)

    @Produces
    fun setItemStateUseCase(
            getShoppingList: GetShoppingList,
            storeShoppingList: StoreShoppingList
    ) : SetItemStateUseCase = SetItemStateUseCase(getShoppingList, storeShoppingList)

    @Produces
    fun deleteItemUseCase(
            getShoppingList: GetShoppingList,
            storeShoppingList: StoreShoppingList
    ) : DeleteItemUseCase = DeleteItemUseCase(getShoppingList, storeShoppingList)
}