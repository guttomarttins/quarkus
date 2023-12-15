package br.com.gt.rest;

import br.com.gt.domain.model.Followers;
import br.com.gt.domain.model.User;
import br.com.gt.domain.repository.FollowersRepository;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.FollowersRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowersResource.class)
class FollowerResourceTest {

    @Inject
    private UserRepository userRepository;
    @Inject
    private FollowersRepository followersRepository;

    private Long userId;
    private Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerEntity = new Followers();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followersRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User id")
    public void sameUserAsFollowerTest(){

        var body = new FollowersRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("id", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself!"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new FollowersRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("id", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowersRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("id", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id doen't exist")
    public void userNotFoundWhenListingFollowersTest(){
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("id", userId)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("id", inexistentUserId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){
        given()
                .pathParam("id", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }


}