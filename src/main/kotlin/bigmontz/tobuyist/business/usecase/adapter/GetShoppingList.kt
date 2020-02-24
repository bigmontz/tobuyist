package bigmontz.tobuyist.business.usecase.adapter

import bigmontz.tobuyist.business.entity.Name
import bigmontz.tobuyist.business.entity.ShoppingList

interface GetShoppingList : Adapter<GetShoppingList.Input, GetShoppingList.Output> {
    data class Input(val name: Name)
    data class Output(val shoppingList: ShoppingList?)
}