package bigmontz.tobuyist.business.entity

data class Name(val value : String) : CharSequence by value

data class ShoppingList (
        val name: Name,
        val list : List<Item> = listOf()) : List<Item> by list {

    fun addItem(value: Item) : ShoppingList {
        val newList = list.toMutableList().apply {
            add(value)
        }.toList()
        return ShoppingList(name, newList)
    }

    fun item(itemId: ItemId) : Item? {
        return this.find { it.itemId == itemId }
    }

    fun editItem(itemId: ItemId, editItem: (Item) -> Item) : ShoppingList {
        return item(itemId)?.let {
            val editedItem = editItem(it)
            val indexOfItem = indexOf(it)
            return@let this.editItem(indexOfItem, editedItem)
        } ?: this
    }

    private fun editItem(indexOfItem: Int, editedItem: Item) : ShoppingList {
        val newList = list.toMutableList().apply {
            set(indexOfItem, editedItem)
        }.toList()
        return copy(list = newList)
    }

    fun remove(itemId: ItemId): ShoppingList {
        val newList = list.filter { it.itemId != itemId }
        return copy(list = newList)
    }
}