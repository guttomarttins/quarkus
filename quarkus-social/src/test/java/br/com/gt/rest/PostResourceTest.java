package br.com.gt.rest;

import br.com.gt.domain.model.Followers;
import br.com.gt.domain.model.Post;
import br.com.gt.domain.model.User;
import br.com.gt.domain.repository.FollowersRepository;
import br.com.gt.domain.repository.PostRepository;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.CreatePostRequest;
import br.com.gt.rest.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowersRepository followerRepository;

    @Inject
    private PostRepository postRepository;

    private Long userId;
    private Long userNotFollowerId;
    private Long userFollowerId;

    @BeforeEach
    @Transactional
    public void init(){
        var user = new User();
        user.setAge(20);
        user.setName("Fulaninho");
        userRepository.persist(user);
        userId = user.getId();

        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Terceiro");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Followers follower = new Followers();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for user successfully")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("id", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }


    @Test
    @DisplayName("should return 404 when trying to make a post to inexistent user")
    public void createPostToInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("id", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exists")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("id", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
                .pathParam("id", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){
        var inexistentFollowerId = 999;

        given()
                .pathParam("id", userId)
                .header("followerId", inexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't exist a follower")
    public void listPostNotAFollowerTest(){
        given()
                .pathParam("id", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't exist a follower")
    public void listPostTest(){
        given()
                .pathParam("id", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}