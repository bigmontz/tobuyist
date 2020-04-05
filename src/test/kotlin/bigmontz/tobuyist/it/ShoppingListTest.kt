package bigmontz.tobuyist.it

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.*

@QuarkusTest
class ShoppingListTest {
    private val LIST_PATH: String = "/shopping-list/{name}"
    private val ITEM_PATH: String = "$LIST_PATH/item/{itemId}"
    private val NAME: String = "my-list"
    private val ITEM_ID: UUID = UUID.randomUUID()
    private val ANOTHER_ITEM_ID: UUID = UUID.randomUUID()

    @Test
    fun `should be able to create list, add items, change item state and delete item`() {
        assertAll(
                { `get a non created list should result in a empty list`() } ,
                { `should be able to add a new item to the list`() },
                { `should get the list with the created item`() },
                { `should be able to add another new item to the list`() },
                { `should get the list with two created items`() },
                { `should be able to buy the first item to the list`() },
                { `should get the list with two created items and the first bought`() },
                { `should be able to delete the second item to the list`() },
                { `should get the list with the first bought`() })
    }

    fun `get a non created list should result in a empty list`() {
        given()
            .pathParam("name", NAME)
            .`when`().get(LIST_PATH)
            .then().log().ifValidationFails()
            .statusCode(200)
            .body("name", equalTo(NAME))
            .body("items", equalTo(emptyList<Any>()))
    }

    fun `should be able to add a new item to the list`() {
        val body = mapOf(
                Pair("product", "Cachaça"),
                Pair("unitOfMeasure", "Liter"),
                Pair("quantity", 3))

        given()
                .pathParam("name", NAME)
                .pathParam("itemId", ITEM_ID)
                .body(body)
                .contentType(ContentType.JSON)
                .`when`().post(ITEM_PATH)
                .then().log().ifValidationFails()
                .body("product", equalTo("Cachaça"))
                .body("unitOfMeasure", equalTo("Liter"))
                .body("quantity", equalTo(3))
                .body("uuid", equalTo(ITEM_ID.toString()))
                .body("state", equalTo("Active"))
    }

    fun `should get the list with the created item`() {
        given()
                .pathParam("name", NAME)
                .`when`().get(LIST_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("name", equalTo(NAME))
                .body("items", `is`(listOf(
                        mapOf(
                                Pair("product", "Cachaça"),
                                Pair("unitOfMeasure", "Liter"),
                                Pair("quantity", 3),
                                Pair("uuid", ITEM_ID.toString()),
                                Pair("state","Active")))))
    }

    fun `should be able to add another new item to the list`() {
        val body = mapOf(
                Pair("product", "Pork"),
                Pair("unitOfMeasure", "Kilogram"),
                Pair("quantity", 10))

        given()
                .pathParam("name", NAME)
                .pathParam("itemId", ANOTHER_ITEM_ID)
                .body(body)
                .contentType(ContentType.JSON)
                .`when`().post(ITEM_PATH)
                .then().log().ifValidationFails()
                .body("product", equalTo("Pork"))
                .body("unitOfMeasure", equalTo("Kilogram"))
                .body("quantity", equalTo(10))
                .body("uuid", equalTo(ANOTHER_ITEM_ID.toString()))
                .body("state", equalTo("Active"))
    }

    fun `should get the list with two created items`() {
        given()
                .pathParam("name", NAME)
                .`when`().get(LIST_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("name", equalTo(NAME))
                .body("items", `is`(listOf(
                        mapOf(
                                Pair("product", "Cachaça"),
                                Pair("unitOfMeasure", "Liter"),
                                Pair("quantity", 3),
                                Pair("uuid", ITEM_ID.toString()),
                                Pair("state","Active")),
                        mapOf(
                                Pair("product", "Pork"),
                                Pair("unitOfMeasure", "Kilogram"),
                                Pair("quantity", 10),
                                Pair("uuid", ANOTHER_ITEM_ID.toString()),
                                Pair("state","Active")))))
    }

    fun `should be able to buy the first item to the list`() {
        val body = mapOf(
                Pair("state", "Bought"))

        given()
                .pathParam("name", NAME)
                .pathParam("itemId", ITEM_ID)
                .body(body)
                .contentType(ContentType.JSON)
                .`when`().patch(ITEM_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("product", equalTo("Cachaça"))
                .body("unitOfMeasure", equalTo("Liter"))
                .body("quantity", equalTo(3))
                .body("uuid", equalTo(ITEM_ID.toString()))
                .body("state", equalTo("Bought"))
    }

    fun `should get the list with two created items and the first bought`() {
        given()
                .pathParam("name", NAME)
                .`when`().get(LIST_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("name", equalTo(NAME))
                .body("items", `is`(listOf(
                        mapOf(
                                Pair("product", "Cachaça"),
                                Pair("unitOfMeasure", "Liter"),
                                Pair("quantity", 3),
                                Pair("uuid", ITEM_ID.toString()),
                                Pair("state","Bought")),
                        mapOf(
                                Pair("product", "Pork"),
                                Pair("unitOfMeasure", "Kilogram"),
                                Pair("quantity", 10),
                                Pair("uuid", ANOTHER_ITEM_ID.toString()),
                                Pair("state","Active")))))
    }

    fun `should be able to delete the second item to the list`() {
        given()
                .pathParam("name", NAME)
                .pathParam("itemId", ANOTHER_ITEM_ID)
                .contentType(ContentType.JSON)
                .`when`().delete(ITEM_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("uuid", equalTo(ANOTHER_ITEM_ID.toString()))
    }

    fun `should get the list with the first bought`() {
        given()
                .pathParam("name", NAME)
                .`when`().get(LIST_PATH)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("name", equalTo(NAME))
                .body("items", `is`(listOf(
                        mapOf(
                                Pair("product", "Cachaça"),
                                Pair("unitOfMeasure", "Liter"),
                                Pair("quantity", 3),
                                Pair("uuid", ITEM_ID.toString()),
                                Pair("state","Bought")))))
    }
}