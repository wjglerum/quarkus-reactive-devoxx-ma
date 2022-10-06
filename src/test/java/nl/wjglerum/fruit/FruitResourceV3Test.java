package nl.wjglerum.fruit;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestHTTPEndpoint(FruitResourceV3.class)
public class FruitResourceV3Test {

    @Test
    public void testListEndpoint() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("transfer-encoding", "chunked")
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    public void testNameEndpoint() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("Banana")
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
                .get("Blueberry")
                .then()
                .statusCode(204);
    }

    @Test
    public void testCreateEndpoint() {
        var data = Map.of("name", "Cranberry");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .redirects()
                .follow(false)
                .body(data)
                .when()
                .post()
                .then()
                .statusCode(201)
                .header("location", containsString("/fruits/v3/"))
                .body(emptyString());
    }
}
