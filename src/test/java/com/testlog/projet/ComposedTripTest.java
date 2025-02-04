package com.testlog.projet;

import com.testlog.projet.types.ComposedTrip;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComposedTripTest {

    @Test
    public void testGetTripBounds_withOneSimpleTrip() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip));

        assertEquals("Paris", composedTrip.getDepartureCity(), "Departure city should be Paris");
        assertEquals("Lyon", composedTrip.getArrivalCity(), "Arrival city should be Lyon");
    }

    @Test
    public void testGetTripBounds_withMultipleSimpleTrips() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.TRAIN, 100, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));

        assertEquals("Paris", composedTrip.getDepartureCity(), "Departure city should be Paris");
        assertEquals("Bordeaux", composedTrip.getArrivalCity(), "Arrival city should be Bordeaux");
    }

    @Test
    public void testGetMode_withOneSimpleTrip() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip));

        assertEquals(List.of(TransportationMode.TRAIN), composedTrip.getMode());
    }

    @Test
    public void testGetMode_withMultipleSimpleTrips() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.PLANE, 100, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));

        List<TransportationMode> expectedModes = List.of(TransportationMode.TRAIN, TransportationMode.PLANE);
        List<TransportationMode> actualModes = composedTrip.getMode();

        assertEquals(expectedModes, actualModes);
    }

    @Test
    public void testGetPrice_withOneSimpleTrip() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip));

        assertEquals(100, composedTrip.getPrice());
    }

    @Test
    public void testGetPrice_withMultipleSimpleTrips() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.PLANE, 200, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));

        assertEquals(300, composedTrip.getPrice());
    }

    @Test
    public void testGetTimes_withOneTrip() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip));

        assertEquals(departureTime, composedTrip.getDepartureTime(), "Departure time should be the departure time of the only trip");
        assertEquals(arrivalTime, composedTrip.getArrivalTime(), "Arrival time should be the arrival time of the only trip");
    }

    @Test
    public void testGetTimes_withMultipleTrips() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.PLANE, 200, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));

        assertEquals(departureTime, composedTrip.getDepartureTime(), "Departure time should be the departure time of the first trip");
        assertEquals(arrivalTime.plusHours(1), composedTrip.getArrivalTime(), "Arrival time should be the arrival time of the last trip");
    }

    @Test
    public void testToString() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.PLANE, 200, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));
        String actual = composedTrip.toString();

        String first = "departureCity='Paris', arrivalCity='Lyon', mode=TRAIN, price=100.0";
        String second = "departureCity='Lyon', arrivalCity='Bordeaux', mode=PLANE, price=200.0";
        assertTrue(actual.contains(first), "String representation should contain the first trip");
        assertTrue(actual.contains(second), "String representation should contain the second trip");
    }

    @Test
    public void testGetTrips_withValues() {
        LocalDateTime departureTime = LocalDateTime.now();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(1);

        SimpleTrip trip1 = new SimpleTrip("Paris", "Lyon", TransportationMode.TRAIN, 100, departureTime, arrivalTime);
        SimpleTrip trip2 = new SimpleTrip("Lyon", "Bordeaux", TransportationMode.PLANE, 200, arrivalTime, arrivalTime.plusHours(1));
        ComposedTrip composedTrip = new ComposedTrip(List.of(trip1, trip2));

        List<SimpleTrip> actual = composedTrip.getTrips();
        assertEquals(2, actual.size(), "List should contain 2 trips");
        assertEquals(trip1, actual.get(0), "First trip should be the first trip added");
        assertEquals(trip2, actual.get(1), "Second trip should be the second trip added");
    }
}
