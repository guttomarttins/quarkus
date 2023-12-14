package br.com.gt.rest;

import br.com.gt.domain.model.User;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.CreatePostRequest;
import br.com.gt.rest.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    private UserRepository userRepository;
    private Long userId;

    @BeforeEach
    @Transactional
    public void init(){
        var user = new User();
        user.setAge(20);
        user.setName("Fulaninho");
        userRepository.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("should create a post for user successfully")
    @Order(1)
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("blablabla");

        var response = given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("id", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }


}