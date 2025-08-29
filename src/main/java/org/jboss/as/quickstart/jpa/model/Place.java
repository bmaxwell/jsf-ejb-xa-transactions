package org.jboss.as.quickstart.jpa.model;

import java.util.Random;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "Place")
@Table(name = "PLACE_TABLE")
public class Place {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @SequenceGenerator(name = "EntitySequence", initialValue = 100)
    @GeneratedValue(generator = "EntitySequence")
    @JsonbProperty("id")
    Long id;

    @Column(name = "name")
    @JsonbProperty("name")
    String name;

    public Place() {
    }

    public Place(String name) {
        this.name = name;
    }

    public Place(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Person: id=%d name=%s", id, name);
    }

    private static final String[] PLACES = { "Los Angeles", "Paris", "New York", "Toyoko", "Sidney", "Houston", "Veince",
            "Rome", "Delhi", "Cairo", "Shanghai", "São Paulo" };

    private static final Random RANDOM = new Random();

    public static String nextRandomPlace() {
        return PLACES[RANDOM.nextInt(PLACES.length)];
    }

    public static Place randomPlace() {
        return new Place(nextRandomPlace());
    }
}
