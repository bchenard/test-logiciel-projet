package com.testlog.projet.criteria;

import java.time.Duration;
import java.time.LocalDate;

public record AdditionalCriteria(LocalDate departureDate, double maxPrice, Duration duration,
                                 String originCity, String destinationCity) {
}
