package org.jboss.as.quickstart.jpa.model;

import java.util.List;

public class ListDatabases {

    private List<Place> placeDatabase1;
    private List<Animal> animalDatabase2;

    public ListDatabases() {
    }

    public List<Place> getPlaceDatabase1() {
        return placeDatabase1;
    }

    public void setPlaceDatabase1(List<Place> placeDatabase1) {
        this.placeDatabase1 = placeDatabase1;
    }

    public List<Animal> getAnimalDatabase2() {
        return animalDatabase2;
    }

    public void setAnimalDatabase2(List<Animal> animalDatabase2) {
        this.animalDatabase2 = animalDatabase2;
    }

    @Override
    public String toString() {
        return "ListDatabases [placeDatabase1=" + placeDatabase1 + ", animalDatabase2=" + animalDatabase2 + "]";
    }
}