package nl.wjglerum.fruit;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/fruits/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResourceV1 {

    @GET
    public Uni<List<Fruit>> fruits() {
        return Fruit.listAll(Sort.ascending("name"));
    }

    @GET
    @Path("{id}")
    public Uni<Fruit> fruit(Long id) {
        return Fruit.findById(id);
    }

    @POST
    public Uni<RestResponse<Fruit>> persist(Fruit fruit) {
        return Panache.<Fruit>withTransaction(fruit::persist)
                .log()
                .onItem()
                .transform(inserted -> URI.create("/fruits/v1/" + inserted.id))
                .onItem()
                .transform(RestResponse::created);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorMessage> persistenceException(PersistenceException exception) {
        Log.error(exception.getMessage());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, new ErrorMessage("Failed to store fruit!"));
    }

    public record ErrorMessage(String message) {
    }
}
