package nl.wjglerum.fruit;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/fruits/v3")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResourceV3 {

    @Inject
    FruitRepository fruitRepository;

    @Inject
    @Channel("fruits-out")
    Emitter<Fruit> fruitEmitter;

    @GET
    public Multi<Fruit> fruits() {
        return fruitRepository.streamAll(Sort.ascending("name"));
    }

    @GET
    @Path("/{name}")
    public Uni<Fruit> fruit(String name) {
        return fruitRepository.findByName(name);
    }

    @POST
    @ReactiveTransactional
    public Uni<RestResponse<Fruit>> persist(Fruit fruit) {
        return fruitRepository.persist(fruit)
                .invoke(inserted -> fruitEmitter.send(inserted))
                .map(inserted -> URI.create("/fruits/v3/" + inserted.name))
                .map(RestResponse::created);
    }

    @Incoming("fruits-in")
    public void log(Fruit fruit) {
        Log.infof("Awesome! A new fruit was registered: %s", fruit.name);
    }

    @ServerExceptionMapper
    public RestResponse<FruitResourceV1.ErrorMessage> persistenceException(PersistenceException exception) {
        Log.error(exception.getMessage());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, new FruitResourceV1.ErrorMessage("Failed to store fruit!"));
    }

    public record ErrorMessage(String message) {
    }
}
