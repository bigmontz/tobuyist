package bigmontz.tobuyist.business.usecase.adapter

import bigmontz.tobuyist.business.entity.Name
import bigmontz.tobuyist.business.entity.ShoppingList

interface StoreShoppingList : Adapter<StoreShoppingList.Input, StoreShoppingList.Output> {
    data class Input(val shoppingList: ShoppingList)
    data class Output(val name: Name)
}