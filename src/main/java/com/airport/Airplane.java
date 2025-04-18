package com.airport;

import java.util.Objects;

public class Airplane {
    private final String id;
    private final String model;
    private final Manufacturer manufacturer;
    private final double fuelForKilometer;
    private final double fuelCapacity;
    private double currentFuel;
    private double kilometersFlown;

    public Airplane(
            final String id,
            final String model,
            final Manufacturer manufacturer,
            final double fuelForKilometer,
            final double fuelCapacity) {
        this(id, model, manufacturer, fuelForKilometer, fuelCapacity, fuelCapacity, 0);
    }

    public Airplane(
            final String id,
            final String model,
            final Manufacturer manufacturer,
            final double fuelForKilometer,
            final double fuelCapacity,
            final double currentFuel,
            final double kilometersFlown) {
        if (fuelForKilometer <= 0) {
            throw new IllegalArgumentException("Fuel per kilometer must be positive");
        }
        if (fuelCapacity <= 0) {
            throw new IllegalArgumentException("Fuel capacity must be positive");
        }
        if (currentFuel < 0 || currentFuel > fuelCapacity) {
            throw new IllegalArgumentException("Current fuel must be between 0 and capacity");
        }
        if (kilometersFlown < 0) {
            throw new IllegalArgumentException("Kilometers flown cannot be negative");
        }
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.fuelForKilometer = fuelForKilometer;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.kilometersFlown = kilometersFlown;
    }

    public double fuelNeeded(final double kilometers) {
        return kilometers * this.fuelForKilometer;
    }

    private void checkEnoughFuel(final double fuel) {
        if (fuel >= currentFuel) {
            throw new IllegalStateException("Not enough fuel");
        }
    }

    public void fly(final double kilometers) {
        final double fuel = this.fuelNeeded(kilometers);
        checkEnoughFuel(fuel);
        this.currentFuel -= fuel;
        this.kilometersFlown += kilometers;
    }

    public void refuel(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be less than 0");
        }

        currentFuel += amount;

        if (currentFuel > fuelCapacity) {
            currentFuel = fuelCapacity;
        }
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public double getFuelForKilometer() {
        return fuelForKilometer;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public double getCurrentFuel() {
        return currentFuel;
    }

    public double getKilometersFlown() {
        return kilometersFlown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airplane airplane = (Airplane) o;
        return Double.compare(airplane.fuelForKilometer, fuelForKilometer) == 0 &&
                Double.compare(airplane.fuelCapacity, fuelCapacity) == 0 &&
                Objects.equals(id, airplane.id) &&
                Objects.equals(model, airplane.model) &&
                Objects.equals(manufacturer, airplane.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, fuelForKilometer, fuelCapacity);
    }

    @Override
    public String toString() {
        return String.format(
                "Airplane{id='%s', model='%s', manufacturer=%s, fuel=%.1f/%.1fL, km=%.1f}",
                this.id,
                this.model,
                this.manufacturer.getName(),
                this.currentFuel,
                this.fuelCapacity,
                this.kilometersFlown);
    }
}
