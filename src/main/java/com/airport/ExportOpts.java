package com.airport;

enum AirlineSortOpts {
    BY_NAME,
    BY_KM,
    BY_CAPACITY,
    BY_FUEL,
    NONE
}

enum AirportSortOpts {
    BY_NAME,
    NONE
}

public record ExportOpts(
        AirportSortOpts airportSortOpts,
        AirlineSortOpts airlineSortOpts) { }
