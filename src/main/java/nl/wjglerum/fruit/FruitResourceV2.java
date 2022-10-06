package nl.wjglerum.fruit;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/fruits/v2")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FruitResourceV2 {

    @Inject
    FruitService fruitService;

    @GET
    public Uni<Fruit> get() {
        return Uni.createFrom().completionStage(fruitService.getFromAbroad());
    }
}
