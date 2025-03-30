package com.airport;

public class Manufacturer {
    private final String name;
    private final String country;

    public Manufacturer(final String name, final String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
