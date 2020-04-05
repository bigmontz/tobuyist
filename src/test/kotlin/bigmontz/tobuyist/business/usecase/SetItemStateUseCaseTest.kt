package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.*
import bigmontz.tobuyist.business.usecase.adapter.GetShoppingList
import bigmontz.tobuyist.business.usecase.adapter.StoreShoppingList
import bigmontz.tobuyist.business.usecase.exception.ResourceNotFound
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture

internal class SetItemStateUseCaseTest {
    private val shoppingListName = "the shopping list name"

    private lateinit var getShoppingList: GetShoppingList
    private lateinit var storeShoppingList: StoreShoppingList
    private lateinit var useCase: SetItemStateUseCase

    @BeforeEach
    fun before() {
        getShoppingList = mockk()
        storeShoppingList = mockk()
        useCase = SetItemStateUseCase(getShoppingList, storeShoppingList)
    }

    @Nested
    inner class `When the list exists` {

        private val item0 = Item(itemId = ItemId(UUID.randomUUID()),
                unitOfMeasure = UnitOfMeasure.Each,
                quantity = 5,
                product = Product("Cachaça"),
                position = 0,
                state = State.Active)

        private val item1 = Item(
                itemId = ItemId(UUID.randomUUID()),
                unitOfMeasure = UnitOfMeasure.Each,
                quantity = 3,
                product = Product("Paçoca"),
                position = 1,
                state = State.Active)

        private val shoppingList = ShoppingList(
                name = Name(shoppingListName),
                list = listOf(item0, item1))

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(Name(shoppingListName)))
            } returns CompletableFuture.completedFuture(GetShoppingList.Output(shoppingList))
        }

        @Nested
        inner class `and the item exists` {
            private val input = SetItemStateUseCase.Input(
                    shoppingListName = shoppingListName,
                    itemId = shoppingList.list[0].itemId.value,
                    state = State.Bought)


            @Nested
            inner class `and it get stored with success` {

                @BeforeEach
                fun scenario() {
                    every {
                        storeShoppingList.apply(any())
                    } returns CompletableFuture.completedFuture(StoreShoppingList.Output(name = Name(shoppingListName)))
                }

                @Test
                fun `should return the edited item in the output`() {
                    subject(input) { output, _ ->
                        Assertions.assertEquals(SetItemStateUseCase.Output(item = item0.copy(state = State.Bought)),  output)
                    }
                }

                @Test
                fun `should return no output`() {
                    subject(input) { _, throwable ->
                        Assertions.assertNull(throwable)

                    }
                }

                @Test
                fun `should store the list with the edit item`() {
                    subject(input) { _, _ ->
                        verify(exactly = 1) {
                            storeShoppingList.apply(StoreShoppingList.Input(
                                    shoppingList = ShoppingList(
                                            name = Name(shoppingListName),
                                            list = listOf(item0.copy(state = State.Bought), item1))))
                        }
                    }
                }

            }

            @Nested
            inner class `and store fails` {
                private val theException: RuntimeException = RuntimeException()

                @BeforeEach
                fun scenario() {
                    every {
                        storeShoppingList.apply(any())
                    } returns CompletableFuture.supplyAsync { throw theException }
                }

                @Test
                fun `should produce no output` () {
                    subject(input) { output, _ ->
                        Assertions.assertNull(output)
                    }
                }

                @Test
                fun  `should return the exception as the cause`() {
                    subject(input){ _, throwable ->
                        Assertions.assertEquals(theException, throwable?.cause)
                    }
                }
            }
        }

        @Nested
        inner class `and the item doesn't exist` {
            private val input = SetItemStateUseCase.Input(
                    shoppingListName = shoppingListName,
                    itemId = UUID.randomUUID(),
                    state = State.Bought)

            @Test
            fun `should produce no output` () {
                subject(input) { output, _ ->
                    Assertions.assertNull(output)
                }
            }

            @Test
            fun  `should return the ResourceNotFound as the cause`() {
                subject(input){ _, throwable ->
                    Assertions.assertEquals(ResourceNotFound::class.java, throwable?.cause?.javaClass!!)
                }
            }

        }
    }

    @Nested
    inner class `When the list doesn't exist` {
        private val input = SetItemStateUseCase.Input(
                shoppingListName = shoppingListName.plus("123"),
                itemId = UUID.randomUUID(),
                state = State.Bought)

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(Name((input.shoppingListName))))
            } returns CompletableFuture.completedFuture(GetShoppingList.Output(null))
        }

        @Test
        fun `should return no output`() {
            subject(input) { output, _ ->
                Assertions.assertNull(output)
            }
        }

        @Test
        fun `should return ResourceNotFound as the cause` () {
            subject(input) { _, throwable ->
                Assertions.assertEquals(ResourceNotFound::class.java, throwable?.cause?.javaClass)
            }
        }
    }

    @Nested
    inner class `When an exception occurs while get the list` {
        private val input = SetItemStateUseCase.Input(
                shoppingListName = shoppingListName.plus("123"),
                itemId = UUID.randomUUID(),
                state = State.Bought)
        private val theException = RuntimeException()

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(Name((input.shoppingListName))))
            } returns CompletableFuture.supplyAsync { throw theException }
        }

        @Test
        fun `should return no output`() {
            subject(input) { output, _ ->
                Assertions.assertNull(output)
            }
        }

        @Test
        fun `should return theException as the cause` () {
            subject(input) { _, throwable ->
                Assertions.assertEquals(theException, throwable?.cause)
            }
        }
    }

    internal fun subject(input: SetItemStateUseCase.Input, callback: (SetItemStateUseCase.Output?, Throwable?) -> Unit) =
            useCase.apply(input).handle(callback).toCompletableFuture().get()
}