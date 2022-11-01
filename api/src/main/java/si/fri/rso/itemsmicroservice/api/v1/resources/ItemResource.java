package si.fri.rso.itemsmicroservice.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.itemsmicroservice.lib.Item;
import si.fri.rso.itemsmicroservice.services.beans.ItemBean;

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
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

        private Logger log = Logger.getLogger(ItemResource.class.getName());

        @Inject
        private ItemBean itemBean;

        @Context
        protected UriInfo uriInfo;

        @Operation(description = "Get all items.", summary = "Get all items")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "List of items", content = @Content(schema = @Schema(implementation = Item.class, type = SchemaType.ARRAY)), headers = {
                                        @Header(name = "X-Total-Count", description = "Number of objects in list") }) })
        @GET
        public Response getItems() {

                List<Item> itemsMetadata = itemBean.getItemFilter(uriInfo);

                return Response.status(Response.Status.OK).entity(itemsMetadata).build();
        }

        @Operation(description = "Get item.", summary = "Get item")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "Item data", content = @Content(schema = @Schema(implementation = Item.class))) })
        @GET
        @Path("/{itemId}")
        public Response getItem(
                        @Parameter(description = "user ID.", required = true) @PathParam("itemId") Integer itemId) {

                Item item = itemBean.getItem(itemId);

                if (item == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.OK).entity(item).build();
        }

        @Operation(description = "Add item.", summary = "Add item")
        @APIResponses({
                        @APIResponse(responseCode = "201", description = "Item successfully added."),
                        @APIResponse(responseCode = "405", description = "Validation error .")
        })
        @POST
        public Response createItem(
                        @RequestBody(description = "DTO object with item data.", required = true, content = @Content(schema = @Schema(implementation = Item.class))) Item item) {

                if ((item.getName() == null || item.getDescription() == null) || item.getImage() == null) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                } else {
                        item = itemBean.createItem(item);
                }

                return Response.status(Response.Status.CONFLICT).entity(item).build();

        }

        @Operation(description = "Update item.", summary = "Update item")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "Item successfully updated.")
        })
        @PUT
        @Path("{itemId}")
        public Response putUser(
                        @Parameter(description = "Item ID.", required = true) @PathParam("itemId") Integer itemId,
                        @RequestBody(description = "DTO object with item.", required = true, content = @Content(schema = @Schema(implementation = Item.class))) Item item) {

                item = itemBean.putItem(itemId, item);

                if (item == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.NOT_MODIFIED).build();

        }

        @Operation(description = "Delete item.", summary = "Delete item")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "User successfully deleted."),
                        @APIResponse(responseCode = "404", description = "Not found.")
        })
        @DELETE
        @Path("{itemId}")
        public Response deleteUser(
                        @Parameter(description = "Item ID.", required = true) @PathParam("itemId") Integer itemId) {

                boolean deleted = itemBean.deleteItem(itemId);

                if (deleted) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }
        }

}
