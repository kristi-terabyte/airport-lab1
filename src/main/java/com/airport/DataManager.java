package com.airport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class DataManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void exportData(Airport airport, Path filePath, ExportOpts opts) throws IOException {
        // Create a copy of the airport with its maxAirlines
        Airport exportAirport = new Airport(airport.getName(), airport.getMaxAirlines());
        exportAirport.setStatus(airport.getStatus()); // Preserve status

        List<Airline> airlines = airport.getAirlines().stream()
                .map(airline -> {
                    // Create new airline with original maxAirplanes
                    Airline newAirline = new Airline(airline.getName(), airline.getMaxAirplanes());
                    // Add airplanes
                    List<Airplane> sortedAirplanes = switch (opts.airlineSortOpts()) {
                        case BY_NAME -> airline.getAirplanes().stream()
                                .sorted(Comparator.comparing(Airplane::getModel))
                                .toList();
                        case BY_KM -> airline.getAirplanes().stream()
                                .sorted(Comparator.comparingDouble(Airplane::getKilometersFlown))
                                .toList();
                        case BY_CAPACITY -> airline.getAirplanes().stream()
                                .sorted(Comparator.comparingDouble(Airplane::getFuelCapacity))
                                .toList();
                        case BY_FUEL -> airline.getAirplanes().stream()
                                .sorted(Comparator.comparingDouble(Airplane::getCurrentFuel))
                                .toList();
                        case NONE -> airline.getAirplanes();
                    };
                    sortedAirplanes.forEach(newAirline::addAirplane);//..(a->newAirline.addAirplane(a));
                    return newAirline;
                })
                .toList();

        if (opts.airportSortOpts() == AirportSortOpts.BY_NAME) {
            airlines = airlines.stream()
                    .sorted(Comparator.comparing(Airline::getName))
                    .toList();
        }

        airlines.forEach(exportAirport::addAirline);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            MAPPER.writeValue(writer, exportAirport);
        }
    }

    public static Airport importData(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            AirportData imported = MAPPER.readValue(reader, AirportData.class);
            if (imported == null) {
                return new Airport("Default Airport", 10);
            }
            Airport airport = new Airport(imported.name, imported.maxAirlines);
            airport.setStatus(imported.status != null ? Airport.Status.valueOf(imported.status) : Airport.Status.OPEN);
            if (imported.airlines != null) {
                for (AirlineData airlineData : imported.airlines) {
                    Airline airline = new Airline(airlineData.name, airlineData.maxAirplanes);
                    if (airlineData.airplanes != null) {
                        for (AirplaneData planeData : airlineData.airplanes) {
                            Manufacturer manufacturer = new Manufacturer(planeData.manufacturer.name, planeData.manufacturer.country);
                            Airplane airplane = new Airplane(
                                    planeData.id,
                                    planeData.model,
                                    manufacturer,
                                    planeData.fuelForKilometer,
                                    planeData.fuelCapacity,
                                    planeData.currentFuel,
                                    planeData.kilometersFlown);
                            airline.addAirplane(airplane);
                        }
                    }
                    airport.addAirline(airline);
                }
            }
            return airport;
        }
    }

    private static class AirportData {
        public String name;
        public int maxAirlines;
        public String status;
        public List<AirlineData> airlines;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AirlineData {
        public String name;
        public int maxAirplanes;
        public List<AirplaneData> airplanes;
    }

    private static class AirplaneData {
        public String id;
        public String model;
        public ManufacturerData manufacturer;
        public double fuelForKilometer;
        public double fuelCapacity;
        public double currentFuel;
        public double kilometersFlown;
    }

    private static class ManufacturerData {
        public String name;
        public String country;
    }
}
