package br.com.gt.rest;

import br.com.gt.domain.model.User;
import br.com.gt.domain.repository.UserRepository;
import br.com.gt.rest.dto.CreateUserRequest;
import br.com.gt.rest.validations.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserRepository repository;

    @Inject
    private Validator validator;

    @POST
    @Transactional
    public Response createUser(CreateUserRequest dto){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(dto);
        if(!violations.isEmpty()){
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY);
        }

        User obj = new User();
        obj.setName(dto.getName());
        obj.setAge(dto.getAge());
        repository.persist(obj);
        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(obj)
                .build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id){
        User user = repository.findById(id);
        if(Objects.nonNull(user)){
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);
        if(Objects.nonNull(user)){
            repository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest dto){
        User user = repository.findById(id);
        if(Objects.nonNull(user)){
            user.setName(dto.getName());
            user.setAge(dto.getAge());
            //repository.persist(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
