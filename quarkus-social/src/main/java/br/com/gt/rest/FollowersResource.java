package br.com.gt.rest;

import br.com.gt.domain.model.Followers;
import br.com.gt.domain.repository.FollowersRepository;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("/users/{id}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowersResource {

    @Inject
    private FollowersRepository repository;

    @Inject
    private UserRepository userRepository;

    @PUT
    @Transactional
    public Response savePost(@PathParam("id") Long id, FollowersRequest dto){

        if(id.equals(dto.getFollowerId())){
            return Response.status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself!")
                    .build();
        }

        var user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(dto.getFollowerId());
        if(Objects.isNull(follower)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean follows = repository.follows(follower, user);

        if(!follows){
            var obj = new Followers();
            obj.setFollower(follower);
            obj.setUser(user);
            repository.persist(obj);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFolowers(@PathParam("id") Long id){

        var user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByUser(id);
        FollowersPerUserResponse responseObj = new FollowersPerUserResponse();
        responseObj.setFollowersCount(list.size());

        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());
        responseObj.setContent(followerList);
        return Response.ok(responseObj).build();
    }

    @GET
    @Path("/follower")
    public Response listUserFolowers(@PathParam("id") Long id){

        var user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByFollower(id);
        FollowersPerUserResponse responseObj = new FollowersPerUserResponse();
        responseObj.setFollowersCount(list.size());

        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());
        responseObj.setContent(followerList);
        return Response.ok(responseObj).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("id") Long id, @QueryParam("followerId") Long followerId){

        var user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
