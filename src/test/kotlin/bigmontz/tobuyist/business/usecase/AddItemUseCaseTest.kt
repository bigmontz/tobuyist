package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.*
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.business.usecase.exception.AlreadyExistsException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CompletableFuture
import org.junit.jupiter.api.Assertions.*
import java.lang.RuntimeException


internal class AddItemUseCaseTest {

    private lateinit var getShoppingList: GetShoppingList
    private lateinit var storeShoppingList: StoreShoppingList
    private lateinit var addItemUseCase: AddItemUseCase

    private val input = AddItemUseCase.Input(
            itemId = UUID.randomUUID(),
            shoppingListName = "Shopping List Name",
            product = "Coca-cola",
            quantity = 10,
            unitOfMeasure = UnitOfMeasure.Liter)

    private val getShoppingListInput = GetShoppingList.Input(Name(input.shoppingListName))

    @BeforeEach
    fun before() {
        getShoppingList = mockk()
        storeShoppingList = mockk()
        addItemUseCase = AddItemUseCase(getShoppingList, storeShoppingList)
    }

    @Nested
    inner class `when the shopping list does not exists` {
        private val expectedItem = Item(
                itemId = ItemId(input.itemId),
                state = State.Active,
                unitOfMeasure = input.unitOfMeasure,
                quantity = input.quantity,
                product = Product(input.product),
                position = 0)

        private val expectedShoppingList = ShoppingList(
                Name(input.shoppingListName),
                listOf(expectedItem))


        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(getShoppingListInput)
            } returns CompletableFuture.completedStage(GetShoppingList.Output(shoppingList = null))
        }

        @Nested
        inner class `and the shopping list is stored with success` {
            private val expectedOutput = AddItemUseCase.Output(
                    shoppingListName = input.shoppingListName,
                    item = expectedItem)

            @BeforeEach
            fun scenario() {
                every {
                    storeShoppingList.apply(StoreShoppingList.Input(shoppingList = expectedShoppingList))
                } returns CompletableFuture.completedStage(StoreShoppingList.Output(expectedShoppingList.name))
            }

            @Test
            fun `it should return no exception`() {
                subject { _, throwable -> assertNull(throwable) }
            }

            @Test
            fun `it should return the expected output`() {
                subject { output, _ -> assertEquals(expectedOutput, output) }
            }

            @Test
            fun `it should store the shopping list with the new item`() {
                subject {_, _ ->
                    verify(exactly = 1) {
                        storeShoppingList.apply(StoreShoppingList.Input(expectedShoppingList))
                    }
                }
            }
        }

        @Nested
        inner class `and store shopping list fail` {
            private val theException: RuntimeException = RuntimeException()

            @BeforeEach
            fun scenario() {
                every {
                    storeShoppingList.apply(StoreShoppingList.Input(shoppingList = expectedShoppingList))
                } returns CompletableFuture.failedStage(theException)
            }

            @Test
            fun `it should return the exception as the cause`() {
                subject { _, throwable -> assertEquals(theException, throwable?.cause) }
            }

            @Test
            fun `it should return no output`() {
                subject { output, _ -> assertNull(output) }
            }
        }

    }

    @Nested
    inner class `when the shopping list already have items` {
        private val storedShoppingList = ShoppingList(
                name = Name(input.shoppingListName),
                list = listOf(
                        Item(
                                itemId = ItemId(UUID.randomUUID()),
                                position = 0,
                                product = Product("Peanuts"),
                                quantity = 10,
                                unitOfMeasure = UnitOfMeasure.Kilogram,
                                state = State.Bought),
                        Item(
                                itemId = ItemId(UUID.randomUUID()),
                                position = 1,
                                product = Product("Coca-cola"),
                                quantity = 5,
                                unitOfMeasure = UnitOfMeasure.Each)))

        private val expectedItem = Item(
                itemId = ItemId(input.itemId),
                state = State.Active,
                unitOfMeasure = input.unitOfMeasure,
                quantity = input.quantity,
                product = Product(input.product),
                position = 2)

        private val expectedShoppingList = storedShoppingList.copy(
                list = storedShoppingList.list.toMutableList().apply { add(expectedItem) }.toList())

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(getShoppingListInput)
            } returns CompletableFuture.completedStage(GetShoppingList.Output(shoppingList = storedShoppingList))
        }

        @Nested
        inner class `and the shopping list is stored with success` {
            private val expectedOutput = AddItemUseCase.Output(
                    shoppingListName = input.shoppingListName,
                    item = expectedItem)

            @BeforeEach
            fun scenario() {
                every {
                    storeShoppingList.apply(StoreShoppingList.Input(shoppingList = expectedShoppingList))
                } returns CompletableFuture.completedStage(StoreShoppingList.Output(expectedShoppingList.name))
            }

            @Test
            fun `it should return no exception`() {
                subject { _, throwable -> assertNull(throwable) }
            }

            @Test
            fun `it should return the expected output`() {
                subject { output, _ -> assertEquals(expectedOutput, output) }
            }

            @Test
            fun `it should store the shopping list with the new item`() {
                subject {_, _ ->
                    verify(exactly = 1) {
                        storeShoppingList.apply(StoreShoppingList.Input(expectedShoppingList))
                    }
                }
            }
        }

        @Nested
        inner class `and the item is already in the stored list` {

            @BeforeEach
            fun scenario() {
                every {
                    getShoppingList.apply(getShoppingListInput)
                } returns CompletableFuture.completedStage(GetShoppingList.Output(shoppingList = expectedShoppingList))
            }

            @Test
            fun `it should throws AlreadyExistsException`() {
                subject { _, throwable -> assertThrows(AlreadyExistsException::class.java) { throw  throwable?.cause!! } }
            }

            @Test
            fun `it should return no output`() {
                subject { output, _ -> assertNull(output) }
            }

            @Test
            fun `it should not store the list`() {
                subject { _, _ ->
                    verify (exactly = 0) {
                        storeShoppingList.apply(any())
                    }
                }
            }

        }

        @Nested
        inner class `and store shopping list fail` {
            private val theException: RuntimeException = RuntimeException()

            @BeforeEach
            fun scenario() {
                every {
                    storeShoppingList.apply(StoreShoppingList.Input(shoppingList = expectedShoppingList))
                } returns CompletableFuture.failedStage(theException)
            }

            @Test
            fun `it should return the exception as the cause`() {
                subject { _, throwable -> assertEquals(theException, throwable?.cause) }
            }

            @Test
            fun `it should return no output`() {
                subject { output, _ -> assertNull(output) }
            }
        }

    }

    fun subject(callback: (AddItemUseCase.Output?, Throwable?) -> Unit) =
            addItemUseCase.apply(input).handle(callback).toCompletableFuture().get()

}