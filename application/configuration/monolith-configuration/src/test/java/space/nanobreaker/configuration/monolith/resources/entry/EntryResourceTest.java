package space.nanobreaker.configuration.monolith.resources.entry;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EntryResourceTest {

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void shouldAccessEntryResourceWhenNotAuthenticated() {
        RestAssured.given()
                .auth().none()
                .when().get("/")
                .then()
                .statusCode(200);
    }
}