package bigmontz.tobuyist.business.usecase

import bigmontz.tobuyist.business.entity.*
import bigmontz.tobuyist.business.usecase.adapter.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture

internal class GetShoppingListUseCaseTest {
    private lateinit var getShoppingList: GetShoppingList
    private lateinit var getShoppingListUseCase: GetShoppingListUseCase

    private val input: GetShoppingListUseCase.Input = GetShoppingListUseCase.Input("List Name")
    private val name: Name = Name(input.name)

    @BeforeEach
    fun before() {
        getShoppingList = mockk()
        getShoppingListUseCase = GetShoppingListUseCase(getShoppingList)
    }

    @Nested
    inner class `When shopping list already exists` {
        private val theShoppingList = ShoppingList(name, listOf(
                Item(
                        itemId = ItemId(UUID.randomUUID()),
                        position = 0,
                        product = Product("Soda"),
                        quantity = 2,
                        unitOfMeasure = UnitOfMeasure.Liter),

                Item(
                        itemId = ItemId(UUID.randomUUID()),
                        position = 1,
                        product = Product("Salami"),
                        quantity = 500,
                        unitOfMeasure = UnitOfMeasure.Gram,
                        state = State.Bought)))

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(name))
            } returns CompletableFuture.completedFuture(GetShoppingList.Output(shoppingList = theShoppingList))
        }

        @Test
        fun `it should return the shopping list`() {
            subject { output, _ ->
                assertEquals(theShoppingList, output?.shoppingList)
            }
        }

        @Test
        fun `it should call the GetShoppingList with the correct input`() {
            subject { _, _ ->
                verify (exactly = 1) {
                    getShoppingList.apply(GetShoppingList.Input(name))
                }
            }
        }

        @Test
        fun `it should return no exception`() {
            subject { _, ex ->
                assertNull(ex)
            }
        }
    }

    @Nested
    inner class `When shopping list doesn't exists` {
        private val theShoppingList: ShoppingList = ShoppingList(name)

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(name))
            } returns CompletableFuture.completedFuture(GetShoppingList.Output(shoppingList = null))
        }

        @Test
        fun `it should return the shopping list`() {
            subject { output, _ ->
                assertEquals(theShoppingList, output?.shoppingList)
            }
        }

        @Test
        fun `it should call the GetShoppingList with the correct input`() {
            subject { _, _ ->
                verify (exactly = 1) {
                    getShoppingList.apply(GetShoppingList.Input(name))
                }
            }
        }

        @Test
        fun `it should return no exception`() {
            subject { _, ex ->
                assertNull(ex)
            }
        }
    }

    @Nested
    inner class `When shopping it get an exception when it getting a list` {
        private val theException: RuntimeException = RuntimeException()

        @BeforeEach
        fun scenario() {
            every {
                getShoppingList.apply(GetShoppingList.Input(name))
            } returns CompletableFuture.supplyAsync { throw theException }
        }

        @Test
        fun `it should return the exception as cause`() {
            subject { _, ex ->
                assertEquals(theException, ex?.cause)
            }
        }

        @Test
        fun `it should return no output`() {
            subject { output, _ ->
                assertNull(output)
            }
        }

        @Test
        fun `it should call the GetShoppingList with the correct input`() {
            subject { _, _ ->
                verify (exactly = 1) {
                    getShoppingList.apply(GetShoppingList.Input(name))
                }
            }
        }
    }

    fun subject(callback: (GetShoppingListUseCase.Output?, Throwable?) -> Unit): Unit =
            getShoppingListUseCase.apply(input).handle(callback).toCompletableFuture().get()
}

