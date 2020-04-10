package bigmontz.tobuyist.configuration

import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.persistence.inmemory.GetShoppingListService
import bigmontz.tobuyist.persistence.inmemory.StoreShoppingListService
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces

@Dependent
class PersistenceConfiguration {

    @Produces
    fun getShoppingList(): GetShoppingList = GetShoppingListService()

    @Produces
    fun storeShoppingList(): StoreShoppingList = StoreShoppingListService()
}