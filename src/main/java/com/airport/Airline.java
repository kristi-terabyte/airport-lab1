package com.airport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Airline {
    private String name;
    private final List<Airplane> airplanes;
    private final int maxAirplanes;

    public Airline(final String name, final int maxAirplanes) {
        if (maxAirplanes <= 0) {
            throw new IllegalArgumentException("Max airplanes must be positive");
        }

        this.name = name;
        this.maxAirplanes = maxAirplanes;
        this.airplanes = new ArrayList<>();
    }

    public void addAirplane(final Airplane airplane) {
        if (airplanes.size() >= maxAirplanes) {
            throw new IllegalStateException("Airline at capacity: " + maxAirplanes);
        }

        airplanes.add(airplane);
    }

    public void removeAirplane(final String id) {
        airplanes.removeIf(a -> a.getId().equals(id));
    }

    public void updateName(final String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = newName;
    }

    public boolean isOperational() {
        return !airplanes.isEmpty();
    }
    public Airplane findAirplane(final String id) {
        return airplanes.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Airplane not found: " + id));
    }

    public List<Airplane> getAirplanes() { return List.copyOf(airplanes); }

    public String getName() {
        return name;
    }

    public int getMaxAirplanes() {
        return maxAirplanes;
    }
}