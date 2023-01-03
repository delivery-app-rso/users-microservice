package si.fri.rso.usersmicroservice.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.lib.UserLoginDto;
import si.fri.rso.usersmicroservice.services.beans.ServicesBean;
import si.fri.rso.usersmicroservice.services.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

        private Logger log = Logger.getLogger(UserResource.class.getName());

        @Inject
        private UserBean userBean;

        @Context
        protected UriInfo uriInfo;

        @Operation(description = "Get all users.", summary = "Get all users")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "List of users", content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY)), headers = {
                                        @Header(name = "X-Total-Count", description = "Number of objects in list") }) })
        @GET
        public Response getUsers() {

                List<User> usersMetadata = userBean.getUserFilter(uriInfo);

                return Response.status(Response.Status.OK).entity(usersMetadata).build();
        }

        @Operation(description = "Get status", summary = "Get status")
        @APIResponses({ @APIResponse(responseCode = "200", description = "status") })
        @GET
        @Path("/status")
        public Response getStatus() {
                return Response.status(Response.Status.OK).build();
        }

        @Operation(description = "Get user.", summary = "Get user")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User data", content = @Content(schema = @Schema(implementation = User.class))),
                        @APIResponse(responseCode = "404", description = "User not found.") })
        @GET
        @Path("/{userId}")
        public Response getUser(
                        @Parameter(description = "user ID.", required = true) @PathParam("userId") Integer userId) {

                User user = userBean.getUser(userId);

                if (user == null) {
                        Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.OK).entity(user).build();
        }

        @Operation(description = "Register user.", summary = "Add user")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User successfully registered."),
                        @APIResponse(responseCode = "404", description = "Bad request.")
        })
        @POST
        @Path("/register")
        public Response register(
                        @RequestBody(description = "DTO object with user data.", required = true, content = @Content(schema = @Schema(implementation = User.class))) User user) {

                if (user.getName() == null || user.getEmail() == null || user.getPassword() == null
                                || user.getAddress() == null) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }

                user = userBean.register(user);

                return Response.status(Response.Status.OK).entity(user).build();

        }

        @Operation(description = "Login user.", summary = "Login user")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User successfully logged in."),
                        @APIResponse(responseCode = "405", description = "Bad request."),
                        @APIResponse(responseCode = "401", description = "Unauthorized access.")
        })
        @POST
        @Path("/login")
        public Response login(
                        @RequestBody(description = "DTO object with user data.", required = true, content = @Content(schema = @Schema(implementation = UserLoginDto.class))) UserLoginDto UserLoginDto) {

                if (UserLoginDto.getEmail() == null || UserLoginDto.getPassword() == null) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }

                User user = userBean.login(UserLoginDto);

                if (user == null) {
                        return Response.status(Response.Status.UNAUTHORIZED).build();
                } else {
                        return Response.status(Response.Status.OK).entity(user).build();
                }
        }

        @Operation(description = "Update user.", summary = "Update user")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User successfully updated."),
                        @APIResponse(responseCode = "404", description = "User not found.")
        })
        @PUT
        @Path("{userId}")
        public Response putUser(
                        @Parameter(description = "User ID.", required = true) @PathParam("userId") Integer userId,
                        @RequestBody(description = "DTO object with user.", required = true, content = @Content(schema = @Schema(implementation = User.class))) User user) {

                user = userBean.putUser(userId, user);

                if (user == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.OK).build();

        }

        @Operation(description = "Delete user.", summary = "Delete user")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User successfully deleted."),
                        @APIResponse(responseCode = "404", description = "Not found.")
        })
        @DELETE
        @Path("{userId}")
        public Response deleteUser(
                        @Parameter(description = "Rating ID.", required = true) @PathParam("userId") Integer userId) {

                boolean deleted = userBean.deleteUser(userId);

                if (deleted) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }
        }

}
