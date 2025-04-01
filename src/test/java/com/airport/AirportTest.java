package com.airport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    @Test
    void testExportData(@TempDir Path tempDir) throws IOException {
        airport.addAirline(airline);
        airline.addAirplane(airplane);
        Path testFile = tempDir.resolve("test-export.json");

        DataManager.exportData(airport, testFile, new ExportOpts(AirportSortOpts.NONE, AirlineSortOpts.NONE));

        // Deserialize JSON to a Map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> exportedData = mapper.readValue(testFile.toFile(),
                new TypeReference<Map<String, Object>>() {
                });

        // Check top-level fields
        assertEquals("Test Airport", exportedData.get("name"));
        assertEquals(2, exportedData.get("maxAirlines"));
        assertEquals("OPEN", exportedData.get("status"));

        // Check airlines list
        List<Map<String, Object>> airlines = (List<Map<String, Object>>) exportedData.get("airlines");
        assertEquals(1, airlines.size());
        Map<String, Object> airlineData = airlines.get(0);
        assertEquals("Test Airline", airlineData.get("name"));
        assertEquals(2, airlineData.get("maxAirplanes"));
        assertEquals(true, airlineData.get("operational")); // Assuming operational is included

        // Check airplanes list
        List<Map<String, Object>> airplanes = (List<Map<String, Object>>) airlineData.get("airplanes");
        assertEquals(1, airplanes.size());
        Map<String, Object> airplaneData = airplanes.get(0);
        assertEquals("A123", airplaneData.get("id"));
        assertEquals("737", airplaneData.get("model"));
        assertEquals(5.0, (Double) airplaneData.get("fuelForKilometer"), 0.01);
        assertEquals(500.0, (Double) airplaneData.get("fuelCapacity"), 0.01);
        assertEquals(500.0, (Double) airplaneData.get("currentFuel"), 0.01);
        assertEquals(0.0, (Double) airplaneData.get("kilometersFlown"), 0.01);

        // Check manufacturer
        Map<String, String> manufacturerData = (Map<String, String>) airplaneData.get("manufacturer");
        assertEquals("Boeing", manufacturerData.get("name"));
        assertEquals("USA", manufacturerData.get("country"));
    }

    @Test
    void testImportData(@TempDir final Path tempDir) throws IOException {
        final Path testFile = tempDir.resolve("test-import.json");
        final String json = String.format(
                "{\"name\":\"Imported Airport\",\"maxAirlines\":3,\"status\":\"OPEN\",\"airlines\":[" +
                        "{\"name\":\"Imported Airline\",\"maxAirplanes\":1,\"airplanes\":[" +
                        "{\"id\":\"I001\",\"model\":\"A320\",\"manufacturer\":{\"name\":\"Airbus\",\"country\":\"France\"},"
                        +
                        "\"fuelForKilometer\":4.0,\"fuelCapacity\":400.0,\"currentFuel\":200.0,\"kilometersFlown\":100.0}"
                        +
                        "]}]}");
        Files.writeString(testFile, json);

        final Airport imported = DataManager.importData(testFile);

        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> importedMap = mapper.readValue(Files.readString(testFile), Map.class);

        assertEquals("Imported Airport", importedMap.get("name"));
        assertEquals(3, importedMap.get("maxAirlines"));
        assertEquals("OPEN", importedMap.get("status"));

        // Check airlines list
        final List<Map<String, Object>> airlines = (List<Map<String, Object>>) importedMap.get("airlines");
        assertEquals(1, airlines.size());

        final Map<String, Object> airlineMap = airlines.get(0);
        assertEquals("Imported Airline", airlineMap.get("name"));
        assertEquals(1, airlineMap.get("maxAirplanes"));

        // Check airplanes list
        final List<Map<String, Object>> airplanes = (List<Map<String, Object>>) airlineMap.get("airplanes");
        assertEquals(1, airplanes.size());

        final Map<String, Object> airplaneMap = airplanes.get(0);
        assertEquals("I001", airplaneMap.get("id"));
        assertEquals("A320", airplaneMap.get("model"));
        assertEquals(4.0, (Double) airplaneMap.get("fuelForKilometer"), 0.01);
        assertEquals(400.0, (Double) airplaneMap.get("fuelCapacity"), 0.01);
        assertEquals(200.0, (Double) airplaneMap.get("currentFuel"), 0.01);
        assertEquals(100.0, (Double) airplaneMap.get("kilometersFlown"), 0.01);

        final Map<String, String> manufacturerMap = (Map<String, String>) airplaneMap.get("manufacturer");
        assertEquals("Airbus", manufacturerMap.get("name"));
        assertEquals("France", manufacturerMap.get("country"));
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

    // Airline Logic Tests
    @Test
    void testAddAirplaneWithinCapacity() {
        airline.addAirplane(airplane);
        assertEquals(1, airline.getAirplanes().size());
        assertEquals(airplane, airline.findAirplane("A123"));
    }

    @Test
    void testAddAirplaneExceedsCapacity() {
        airline.addAirplane(airplane);
        airline.addAirplane(new Airplane("A124", "747", manufacturer, 5.0, 600.0));
        assertThrows(IllegalStateException.class,
                () -> airline.addAirplane(new Airplane("A125", "757", manufacturer, 5.0, 700.0)));
    }

    @Test
    void testRemoveAirplane() {
        airline.addAirplane(airplane);
        airline.removeAirplane("A123");
        assertThrows(IllegalArgumentException.class, () -> airline.findAirplane("A123"));
    }

    @Test
    void testUpdateAirlineName() {
        airline.updateName("New Airline");
        assertEquals("New Airline", airline.getName());
    }

    @Test
    void testUpdateAirlineNameWithEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> airline.updateName(""));
    }

    @Test
    void testIsOperational() {
        assertFalse(airline.isOperational());
        airline.addAirplane(airplane);
        assertTrue(airline.isOperational());
        airline.removeAirplane("A123");
        assertFalse(airline.isOperational());
    }

    // Airplane Logic Tests
    @Test
    void testFlyAirplaneWithEnoughFuel() {
        airplane.fly(50);//currKilometers:0->50,currFuel:500->500-(50*5)
        assertEquals(250.0, airplane.getCurrentFuel(), 0.01);
        assertEquals(50.0, airplane.getKilometersFlown(), 0.01);
    }

    @Test
    void testFlyAirplaneWithoutEnoughFuel() {
        assertThrows(IllegalStateException.class, () -> airplane.fly(200));
    }

    @Test
    void testRefuelAirplane() {
        airplane.fly(50);
        airplane.refuel(50);
        assertEquals(300.0, airplane.getCurrentFuel(), 0.01);
    }

    @Test
    void testRefuelAirplaneOverCapacity() {
        airplane.refuel(1000);
        assertEquals(500.0, airplane.getCurrentFuel(), 0.01);
    }

    @Test
    void testAirplaneConstructorWithInvalidFuelValues() {
        assertThrows(IllegalArgumentException.class, () -> new Airplane("A124", "747", manufacturer, -5.0, 500.0));
        assertThrows(IllegalArgumentException.class, () -> new Airplane("A124", "747", manufacturer, 5.0, -500.0));
    }
}