package org.jboss.as.quickstart.jpa.model;

public class ClearDatabases {

    private String placeDatabase1;
    private String animalDatabase2;

    public ClearDatabases() {
    }

    public String getPlaceDatabase1() {
        return placeDatabase1;
    }

    public void setPlaceDatabase1(String placeDatabase1) {
        this.placeDatabase1 = placeDatabase1;
    }

    public String getAnimalDatabase2() {
        return animalDatabase2;
    }

    public void setAnimalDatabase2(String animalDatabase2) {
        this.animalDatabase2 = animalDatabase2;
    }

    @Override
    public String toString() {
        return "ClearDatabases [placeDatabase1=" + placeDatabase1 + ", animalDatabase2=" + animalDatabase2 + "]";
    }
}