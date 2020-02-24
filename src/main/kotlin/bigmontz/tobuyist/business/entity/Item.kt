package bigmontz.tobuyist.business.entity

import java.util.*

data class Product(val value : String) : CharSequence by value
data class ItemId(val value: UUID)

enum class UnitOfMeasure {
    Each,
    Gram,
    Kilogram,
    Liter
}


enum class State {
    Active,
    Bought
}

data class Item (
        val itemId : ItemId,
        val position: Int,
        val product : Product,
        val quantity : Int,
        val unitOfMeasure : UnitOfMeasure = UnitOfMeasure.Each,
        val state : State  = State.Active)