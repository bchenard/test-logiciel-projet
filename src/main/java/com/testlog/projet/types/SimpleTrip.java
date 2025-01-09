package com.testlog.projet.types;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record SimpleTrip(String departureCity, String arrivalCity, TransportationMode mode, double price,
                         LocalDateTime departureTime, LocalDateTime arrivalTime) {
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "{" +
                "departureCity='" + departureCity + '\'' +
                ", arrivalCity='" + arrivalCity + '\'' +
                ", mode=" + mode +
                ", price=" + price +
                ", departureTime=" + departureTime.format(formatter) +
                ", arrivalTime=" + arrivalTime.format(formatter) +
                '}';
    }
}