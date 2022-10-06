package nl.wjglerum.fruit;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Fruit extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String name;

    public static Fruit of(String name) {
        var fruit = new Fruit();
        fruit.name = name;
        return fruit;
    }

    public static Uni<Fruit> findByName(String name) {
        return Fruit.find("name", name).firstResult();
    }
}
