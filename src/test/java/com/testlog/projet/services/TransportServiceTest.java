package com.testlog.projet.services;

import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransportServiceTest {

    private Instant toInstant(LocalDate date, LocalTime time) {
        return time.atDate(date).atZone(ZoneId.systemDefault()).toInstant();
    }

    @Test
    public void getForCity_returnsExpectedTrips() {
        TransportService transportService = new TransportService();

        List<SimpleTrip> trips = transportService.getForCity("TestCity");

        assertNotNull(trips);
        assertFalse(trips.isEmpty());

        SimpleTrip trip = trips.get(0);
        assertEquals("TestCity", trip.departureCity());
        assertEquals("TestDestination", trip.arrivalCity());

        assertEquals(TransportationMode.TRAIN, trip.mode());
        assertEquals(10.0, trip.price());

        assertEquals(toInstant(LocalDate.now(), LocalTime.of(8, 0)), trip.departureTime());
        assertEquals(toInstant(LocalDate.now(), LocalTime.of(10, 0)), trip.arrivalTime());
    }

    @Test
    public void getForCity_returnsEmptyListForNonexistentCity() {
        TransportService transportService = new TransportService();

        List<SimpleTrip> trips = transportService.getForCity("NonexistentCity");

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }
}