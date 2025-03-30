package com.airport;

import java.util.ArrayList;
import java.util.List;

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

    public Airline findAirline(final String name) {
        return airlines.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Airline not found: " + name));
    }
}
