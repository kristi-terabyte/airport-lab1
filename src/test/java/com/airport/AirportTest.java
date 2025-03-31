package com.airport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportTest {
    private Airport airport;
    private Airline airline;
    private Airplane airplane;
    private Manufacturer manufacturer;

    @BeforeEach
    void setUp() {
        airport = new Airport("Test Airport", 2);
        airline = new Airline("Test Airline", 2);
        manufacturer = new Manufacturer("Boeing", "USA");
        airplane = new Airplane("A123", "737", manufacturer, 5.0, 500.0);
    }

    // Airport Logic Tests
    @Test
    void testAddAirlineWithinCapacity() {
        airport.addAirline(airline);
        assertEquals(1, airport.getAirlines().size());
        assertEquals(airline, airport.findAirline("Test Airline"));
    }

    @Test
    void testAddAirlineExceedsCapacity() {
        airport.addAirline(new Airline("Airline1", 1));
        airport.addAirline(new Airline("Airline2", 1));
        assertThrows(IllegalStateException.class, () -> airport.addAirline(new Airline("Airline3", 1)));
    }

    @Test
    void testAddAirlineWhenClosed() {
        airport.toggleStatus(); // Set to CLOSED
        assertThrows(IllegalStateException.class, () -> airport.addAirline(airline));
    }

    @Test
    void testRemoveAirline() {
        airport.addAirline(airline);
        airport.removeAirline("Test Airline");
        assertThrows(IllegalArgumentException.class, () -> airport.findAirline("Test Airline"));
    }

    @Test
    void testRemoveAirlineWhenClosed() {
        airport.addAirline(airline);
        airport.toggleStatus(); // Set to CLOSED
        assertThrows(IllegalStateException.class, () -> airport.removeAirline("Test Airline"));
    }

    @Test
    void testUpdateName() {
        airport.updateName("New Airport");
        assertEquals("New Airport", airport.getName());
    }

    @Test
    void testUpdateNameWithEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> airport.updateName(""));
    }

    @Test
    void testToggleStatus() {
        assertEquals(Airport.Status.OPEN, airport.getStatus());
        airport.toggleStatus();
        assertEquals(Airport.Status.CLOSED, airport.getStatus());
        airport.toggleStatus();
        assertEquals(Airport.Status.OPEN, airport.getStatus());
    }

    @Test
    void testFindNonExistingAirlineThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> airport.findAirline("NonExistent"));
    }
}