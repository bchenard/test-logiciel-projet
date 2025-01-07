package com.testlog.projet.criteria;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdditionalCriteria(LocalDateTime departureDate, double maxPrice, Duration duration,
                                 String originCity, String destinationCity) {
}
