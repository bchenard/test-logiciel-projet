package com.testlog.projet.types;

import java.time.Instant;
import java.time.LocalDateTime;

public record SimpleTrip(String departureCity, String arrivalCity, TransportationMode mode, double price,
                         LocalDateTime departureTime, LocalDateTime arrivalTime) {
}
