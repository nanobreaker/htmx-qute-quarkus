package space.nanobreaker;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class BoardResourceTest {

    @Test
    public void testGetBoardResource() {
        given()
                .when().get("/board")
                .then()
                .statusCode(200);
    }

}