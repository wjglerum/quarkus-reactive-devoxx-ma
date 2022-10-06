package nl.wjglerum.fruit;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(FruitResourceV1.class)
public class FruitResourceV1Test {

    @Test
    public void testListEndpoint() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    public void testIdEndpoint() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("3")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("Banana"));
    }

    @Test
    public void testNoneEndpoint() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("100")
                .then()
                .statusCode(204);
    }

    @Test
    public void testCreateEndpoint() {
        var data = Map.of("name", "Pineapple");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("location", containsString("/fruits/v1/"))
                .body(emptyString());
    }
}
