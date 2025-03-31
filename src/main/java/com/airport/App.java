package com.airport;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static final String HELP_MESSAGE = """
            Airport Management CLI
            Usage: cli <command> [subcommand] [args]

            Commands:
              new <name> <maxAirlines>          - Create a new airport
              update airport <newName>          - Rename the airport
              update status                     - Toggle airport open/closed
              add airline <name> <maxAirplanes> - Add an airline with capacity
              update airline <oldName> <newName>
              remove airline <name>
              add airplane <airline> <id> <model> <manufacturer> <country> <fuelPerKm> <capacity>
              remove airplane <airline> <id>
              info airline <name>
              info airplane <airline> <id>
              refuel <airline> <id> [<amount>]
              fly <airline> <id> <kilometers>
              list
              list <airline>
              help
              help <command>
              exit (interactive mode only)
            """;

    private static final String HELP_NEW = "new <name> <maxAirlines>";
    private static final String HELP_UPDATE = """
            update airport <newName>
            update status
            update airline <oldName> <newName>
            """;
    private static final String HELP_ADD = """
            add airline <name> <maxAirplanes>
            add airplane <airline> <id> <model> <manufacturer> <country> <fuelPerKm> <capacity>
            """;
    private static final String HELP_REMOVE = """
            remove airline <name>
            remove airplane <airline> <id>
            """;
    private static final String HELP_INFO = """
            info airline <name>
            info airplane <airline> <id>
            """;
    private static final String HELP_REFUEL = "refuel <airline> <id> [<amount>]";
    private static final String HELP_FLY = "fly <airline> <id> <kilometers>";
    private static final String HELP_LIST = "list\nlist <airline>";

    private static Airport airport = new Airport("Global Airport", 10); // Default capacity

    public static void main(final String[] args) {
        if (args.length == 0) {
            runInteractiveMode();
        } else {
            processCommand(args);
        }
    }

    private static void runInteractiveMode() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Entering interactive mode. Type 'help' for commands or 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                final String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting interactive mode.");
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                final String[] args = splitCommand(input);
                processCommand(args);
            }
        }
    }

    private static String[] splitCommand(final String input) {
        return input.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private static void processCommand(final String[] args) {
        try {
            switch (args[0].toLowerCase()) {
                case "new" -> handleNew(args);
                case "update" -> handleUpdate(args);
                case "add" -> handleAdd(args);
                case "remove" -> handleRemove(args);
                case "info" -> handleInfo(args);
                case "refuel" -> handleRefuel(args);
                case "fly" -> handleFly(args);
                case "list" -> handleList(args);
                case "help" -> handleHelp(args);
                default -> {
                    System.out.println("Unknown command: " + args[0]);
                    printHelp();
                }
            }
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println(HELP_MESSAGE);
    }

    private static void handleNew(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: " + HELP_NEW);
        }
        airport = new Airport(args[1], Integer.parseInt(args[2]));
        System.out.println("Created new airport: " + args[1] + " (Max airlines: " + args[2] + ")");
    }

    private static void handleUpdate(final String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: " + HELP_UPDATE);
        }
        switch (args[1].toLowerCase()) {
            case "airport" -> {
                if (args.length != 3) throw new IllegalArgumentException("Usage: update airport <newName>");
                airport.updateName(args[2]);
                System.out.println("Renamed airport to: " + args[2]);
            }
            case "status" -> {
                if (args.length != 2) throw new IllegalArgumentException("Usage: update status");
                airport.toggleStatus();
                System.out.println("Airport status updated to: " + airport.getStatus());
            }
            case "airline" -> {
                if (args.length != 4) throw new IllegalArgumentException("Usage: update airline <oldName> <newName>");
                airport.findAirline(args[2]).updateName(args[3]);
                System.out.println("Renamed airline " + args[2] + " to " + args[3]);
            }
            default -> throw new IllegalArgumentException("Unknown subcommand: " + args[1]);
        }
    }

    private static void handleAdd(final String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: " + HELP_ADD);
        }
        switch (args[1].toLowerCase()) {
            case "airline" -> {
                if (args.length != 4) {
                    throw new IllegalArgumentException("Usage: add airline <name> <maxAirplanes>");
                }
                final Airline airline = new Airline(args[2], Integer.parseInt(args[3]));
                airport.addAirline(airline);
                System.out.println("Added airline: " + args[2] + " (Max airplanes: " + args[3] + ")");
            }
            case "airplane" -> {
                if (args.length != 9) {
                    throw new IllegalArgumentException(
                            "Usage: add airplane <airline> <id> <model> <manufacturer> <country> <fuelPerKm> <capacity>");
                }
                final Airline airline = airport.findAirline(args[2]);
                final Manufacturer manufacturer = new Manufacturer(args[5], args[6]);
                final Airplane airplane = new Airplane(
                        args[3], args[4], manufacturer, Double.parseDouble(args[7]), Double.parseDouble(args[8]));
                airline.addAirplane(airplane);
                System.out.println("Added to " + args[2] + ": " + airplane);
            }
            default -> throw new IllegalArgumentException("Unknown subcommand: " + args[1]);
        }
    }

    private static void handleRemove(final String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: " + HELP_REMOVE);
        }
        switch (args[1].toLowerCase()) {
            case "airline" -> {
                if (args.length != 3) throw new IllegalArgumentException("Usage: remove airline <name>");
                airport.removeAirline(args[2]);
                System.out.println("Removed airline: " + args[2]);
            }
            case "airplane" -> {
                if (args.length != 4) throw new IllegalArgumentException("Usage: remove airplane <airline> <id>");
                airport.findAirline(args[2]).removeAirplane(args[3]);
                System.out.println("Removed airplane " + args[3] + " from " + args[2]);
            }
            default -> throw new IllegalArgumentException("Unknown subcommand: " + args[1]);
        }
    }

    private static void handleInfo(final String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: " + HELP_INFO);
        }
        switch (args[1].toLowerCase()) {
            case "airline" -> {
                if (args.length != 3) throw new IllegalArgumentException("Usage: info airline <name>");
                System.out.println(airport.findAirline(args[2]));
            }
            case "airplane" -> {
                if (args.length != 4) throw new IllegalArgumentException("Usage: info airplane <airline> <id>");
                System.out.println(airport.findAirline(args[2]).findAirplane(args[3]));
            }
            default -> throw new IllegalArgumentException("Unknown subcommand: " + args[1]);
        }
    }

    private static void handleRefuel(final String[] args) throws IOException {
        if (args.length < 3 || args.length > 4) {
            throw new IllegalArgumentException("Usage: " + HELP_REFUEL);
        }
        final Airplane airplane = airport.findAirline(args[1]).findAirplane(args[2]);
        final double amount = args.length == 4 ? Double.parseDouble(args[3]) : airplane.getFuelCapacity() - airplane.getCurrentFuel();
        airplane.refuel(amount);
        System.out.println(String.format("Refueled %s in %s by %.1f", args[2], args[1], amount));
    }

    private static void handleFly(final String[] args) throws IOException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: " + HELP_FLY);
        }
        if (airport.getStatus() == Airport.Status.CLOSED) {
            throw new IllegalStateException("Airport is closed");
        }
        final Airline airline = airport.findAirline(args[1]);
        if (!airline.isOperational()) {
            throw new IllegalStateException("Airline " + args[1] + " is not operational (no airplanes)");
        }
        airline.findAirplane(args[2]).fly(Double.parseDouble(args[3]));
        System.out.println(String.format("Flew %s in %s for %s km", args[2], args[1], args[3]));
    }

    private static void handleList(final String[] args) {
        if (args.length == 1) {
            final var airlines = airport.getAirlines();
            if (airlines.isEmpty()) {
                System.out.println("No airlines in the airport.");
                return;
            }
            System.out.println(airport);
            airlines.forEach(a -> System.out.println("  " + a));
            return;
        }
        if (args.length == 2) {
            final Airline airline = airport.findAirline(args[1]);
            final var airplanes = airline.getAirplanes();
            if (airplanes.isEmpty()) {
                System.out.println("No airplanes in " + args[1] + ".");
                return;
            }
            System.out.println(airline);
            airplanes.forEach(a -> System.out.println("  " + a));
            return;
        }
        throw new IllegalArgumentException("Usage: " + HELP_LIST);
    }

    private static void handleHelp(final String[] args) {
        if (args.length == 1) {
            printHelp();
            return;
        }
        switch (args[1].toLowerCase()) {
            case "new" -> System.out.println(HELP_NEW);
            case "update" -> System.out.println(HELP_UPDATE);
            case "add" -> System.out.println(HELP_ADD);
            case "remove" -> System.out.println(HELP_REMOVE);
            case "info" -> System.out.println(HELP_INFO);
            case "refuel" -> System.out.println(HELP_REFUEL);
            case "fly" -> System.out.println(HELP_FLY);
            case "list" -> System.out.println(HELP_LIST);
            case "help" -> System.out.println("help\nhelp <command>");
            default -> {
                System.out.println("Unknown help topic: " + args[1]);
                printHelp();
            }
        }
    }
}