package com.airport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Airport {
    private String name;
    private final List<Airline> airlines;
    private Status status;
    private final int maxAirlines;

    public enum Status {
        OPEN, CLOSED
    }

    public Airport(final String name, final int maxAirlines) {
        if (maxAirlines <= 0)
            throw new IllegalArgumentException("Max airlines must be positive");
        this.name = name;
        this.maxAirlines = maxAirlines;
        this.airlines = new ArrayList<>();
        this.status = Status.OPEN;
    }

    private void checkStatus() {
        if (status == Status.CLOSED) {
            throw new IllegalStateException("Airport is closed");
        }
    }

    public void addAirline(final Airline airline) {
        checkStatus();

        if (airlines.size() >= maxAirlines) {
            throw new IllegalStateException("Airport at capacity: " + maxAirlines);
        }

        airlines.add(airline);
    }

    public void removeAirline(final String name) {
        checkStatus();

        airlines.removeIf(a -> a.getName().equals(name));
    }

    public void updateName(final String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = newName;
    }

    public void toggleStatus() {
        this.status = (status == Status.OPEN) ? Status.CLOSED : Status.OPEN;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Airline findAirline(final String name) {
        return airlines.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Airline not found: " + name));
    }

    public List<Airline> getAirlines() {
        return List.copyOf(airlines);
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public int getMaxAirlines() {
        return maxAirlines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return maxAirlines == airport.maxAirlines && Objects.equals(name, airport.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxAirlines);
    }

    @Override
    public String toString() {
        return "Airport: " + name + "\nAirlines: " + airlines.size();
    }
}
