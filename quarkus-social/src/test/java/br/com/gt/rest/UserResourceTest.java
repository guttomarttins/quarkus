package br.com.gt.rest;

import br.com.gt.rest.dto.CreateUserRequest;
import br.com.gt.rest.validations.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL USERS_API;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setAge(100);
        user.setName("Beltrano");

        var response = given()
               .contentType(ContentType.JSON)
                .body(user)
              .when()
               .post(USERS_API)
              .then()
                .extract().response();

        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(USERS_API)
                .then()
                .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));
        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(0).get("field"));
        String field1 = errors.get(0).get("field");
        String message1 = errors.get(0).get("message");
        assertTrue(field1.equals("name") || field1.equals("age"));
        assertTrue(message1.equals("Name is required!") || message1.equals("Age is required!"));
        assertNotNull(errors.get(1).get("message"));
        assertNotNull(errors.get(1).get("field"));
        String field2 = errors.get(1).get("field");
        String message2 = errors.get(1).get("message");
        assertTrue(field2.equals("name") || field2.equals("age"));
        assertTrue(message2.equals("Name is required!") || message2.equals("Age is required!"));

        //assertEquals("Name is required!", errors.get(0).get("message"));
        //assertEquals("age", errors.get(1).get("field"));
        //assertEquals("Age is required!", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void listAllUsersTest(){


         given()
                .contentType(ContentType.JSON)
                .when()
                .get(USERS_API)
                .then()
                 .statusCode(200)
                 .body("size()", Matchers.is(1));
    }

}