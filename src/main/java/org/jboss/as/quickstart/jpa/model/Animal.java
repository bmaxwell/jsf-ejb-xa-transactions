package org.jboss.as.quickstart.jpa.model;

import java.util.Random;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "Animal")
@Table(name = "ANIMAL_TABLE")
public class Animal {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "EntitySequence", initialValue = 100)
    @GeneratedValue(generator = "EntitySequence")
    @JsonbProperty("id")
    Long id;

    @Column(name = "name")
    @JsonbProperty("name")
    String name;

    public Animal() {
    }

    public Animal(String name) {
        this.name = name;
    }

    public Animal(Long id, String name) {
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
        return String.format("Animal: id=%d name=%s", id, name);
    }

    private static final String[] ANIMALS = { "Polar Bear", "Zebra", "Elk", "Dog", "Cat", "Tiger", "Mouse", "Horse", "Bird",
            "Lion", "Rat", "Emu", "Koala" };
    private static final Random RANDOM = new Random();

    public static String nextRandomAnimal() {
        return ANIMALS[RANDOM.nextInt(ANIMALS.length)];
    }

    public static Animal randomAnimal() {
        return new Animal(nextRandomAnimal());
    }
}