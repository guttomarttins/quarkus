package br.com.gt.rest;

import br.com.gt.domain.model.Post;
import br.com.gt.domain.model.User;
import br.com.gt.domain.repository.FollowersRepository;
import br.com.gt.domain.repository.PostRepository;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.CreatePostRequest;
import br.com.gt.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;
import java.util.stream.Collectors;

@Path("/users/{id}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    private PostRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowersRepository followersRepository;

    @POST
    @Transactional
    public Response savePost(@PathParam("id") Long id, CreatePostRequest dto){

        User user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post obj = new Post();
        obj.setText(dto.getText());
        obj.setUser(user);

        repository.persist(obj);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("id") Long id, @HeaderParam("followerId") Long followerId){

        User user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(Objects.isNull(followerId)){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if(Objects.isNull(follower)){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Inexistent followerId")
                    .build();
        }

        boolean follows = followersRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts")
                    .build();
        }

        PanacheQuery<Post> query = repository.find("user",
                Sort.by("dateTime", Sort.Direction.Descending), user);

        var list = query.list();
        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());
        return Response.ok(postResponseList).build();
    }
}
