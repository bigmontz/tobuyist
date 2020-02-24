package bigmontz.tobuyist.persistence.inmemory

import bigmontz.tobuyist.business.entity.Name
import bigmontz.tobuyist.business.entity.ShoppingList

open class ShoppingListStorageClass(
        private val map:  MutableMap<Name, ShoppingList> = mutableMapOf()
): Map<Name, ShoppingList> by map {
    override fun toString(): String {
        return map.toString()
    }

    operator fun set(name: Name, value: ShoppingList) {
        map[name] = value
    }
}

object ShoppingListStorage : ShoppingListStorageClass()