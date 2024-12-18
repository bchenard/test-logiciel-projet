package com.testlog.projet.types;

import java.time.Instant;

public record SimpleTrip(String departureCity, String arrivalCity, TransportationMode mode, double price,
                         Instant departureTime, Instant arrivalTime) {
}
