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

internal class DeleteItemUseCaseTest {
    private val shoppingListName = "the shopping list name"

    private lateinit var getShoppingList: GetShoppingList
    private lateinit var storeShoppingList: StoreShoppingList
    private lateinit var deleteItemUseCase: DeleteItemUseCase

    @BeforeEach
    fun before () {
        getShoppingList = mockk()
        storeShoppingList = mockk()
        deleteItemUseCase = DeleteItemUseCase(getShoppingList, storeShoppingList)
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
            } returns CompletableFuture.completedStage( GetShoppingList.Output(shoppingList))
        }

        @Nested
        inner class `and the item exists` {
            private val input = DeleteItemUseCase.Input(
                    shoppingListName = shoppingListName,
                    itemId = shoppingList.list[0].itemId.value)


            @Nested
            inner class `and it get stored with success` {

                @BeforeEach
                fun scenario() {
                    every {
                        storeShoppingList.apply(any())
                    } returns CompletableFuture.completedStage(StoreShoppingList.Output(name = Name(shoppingListName)))
                }

                @Test
                fun `should return the id of the delete item in the output`() {
                    subject(input) { output, _ ->
                        Assertions.assertEquals(DeleteItemUseCase.Output(itemId = input.itemId),  output)
                    }
                }

                @Test
                fun `should return no output`() {
                    subject(input) { _, throwable ->
                        Assertions.assertNull(throwable)

                    }
                }

                @Test
                fun `should store the list without the delete item`() {
                    subject(input) { _, _ ->
                        verify(exactly = 1) {
                            storeShoppingList.apply(StoreShoppingList.Input(
                                    shoppingList = ShoppingList(
                                            name = Name(shoppingListName),
                                            list = listOf(item1))))
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
                    } returns CompletableFuture.failedFuture(theException)
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
            private val input = DeleteItemUseCase.Input(
                    shoppingListName = shoppingListName,
                    itemId = UUID.randomUUID())


            @Nested
            inner class `and it get stored with success` {

                @BeforeEach
                fun scenario() {
                    every {
                        storeShoppingList.apply(any())
                    } returns CompletableFuture.completedStage(StoreShoppingList.Output(name = Name(shoppingListName)))
                }

                @Test
                fun `should return the id of the delete item in the output`() {
                    subject(input) { output, _ ->
                        Assertions.assertEquals(DeleteItemUseCase.Output(itemId = input.itemId),  output)
                    }
                }

                @Test
                fun `should return no output`() {
                    subject(input) { _, throwable ->
                        Assertions.assertNull(throwable)

                    }
                }

                @Test
                fun `should store the list`() {
                    subject(input) { _, _ ->
                        verify(exactly = 1) {
                            storeShoppingList.apply(StoreShoppingList.Input(
                                    shoppingList = shoppingList))
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
                    } returns CompletableFuture.failedFuture(theException)
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
    }

    @Nested
    inner class `When the list doesn't exist` {
        private val input = DeleteItemUseCase.Input(
                shoppingListName = shoppingListName.plus("123"),
                itemId = UUID.randomUUID())

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(Name((input.shoppingListName))))
            } returns CompletableFuture.completedStage(GetShoppingList.Output(null))
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
        private val input = DeleteItemUseCase.Input(
                shoppingListName = shoppingListName.plus("123"),
                itemId = UUID.randomUUID())
        private val theException = RuntimeException()

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(Name((input.shoppingListName))))
            } returns CompletableFuture.failedFuture(theException)
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


    internal fun subject(input: DeleteItemUseCase.Input, callback: (DeleteItemUseCase.Output?, Throwable?) -> Unit) =
            deleteItemUseCase.apply(input).handle(callback).toCompletableFuture().get()
}