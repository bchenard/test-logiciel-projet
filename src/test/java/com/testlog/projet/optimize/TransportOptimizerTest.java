package com.testlog.projet.optimize;

import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.ComposedTrip;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransportOptimizerTest {

    final LocalDateTime departure = LocalDateTime.parse("2025-01-08T08:00:00"); // Wednesday
    final Duration duration = Duration.ofDays(1);
    final LocalDateTime returnDate = departure.plus(duration);
    final TransportCriteria transportCriteriaTrain = new TransportCriteria(TransportationMode.TRAIN, true);
    final TransportCriteria transportCriteriaPlane = new TransportCriteria(TransportationMode.PLANE, true);
    final TransportCriteria transportCriteriaNotSpecified = new TransportCriteria(TransportationMode.NOT_SPECIFIED, true);
    final TransportCriteria transportCriteriaDuration = new TransportCriteria(TransportationMode.NOT_SPECIFIED, false);
    final double maxPrice = 2000.;
    final AdditionalCriteria other = new AdditionalCriteria(departure, maxPrice, duration, "origin", "destination");
    ICityService<SimpleTrip> transportService;
    TransportOptimizer transportOptimizer;
    SimpleTrip forwardTripTrain;
    SimpleTrip backwardTripTrain;
    SimpleTrip forwardTripPlane;
    SimpleTrip backwardTripPlane;

    @BeforeEach
    public void setUp() {
        transportService = mock(ICityService.class);
        transportOptimizer = new TransportOptimizer(transportService);

        forwardTripTrain = new SimpleTrip("origin", "destination", TransportationMode.TRAIN, 100., departure, departure.plusHours(2));
        backwardTripTrain = new SimpleTrip("destination", "origin", TransportationMode.TRAIN, 100., returnDate, returnDate.plusHours(2));
        forwardTripPlane = new SimpleTrip("origin", "destination", TransportationMode.PLANE, 100., departure, departure.plusHours(1));
        backwardTripPlane = new SimpleTrip("destination", "origin", TransportationMode.PLANE, 300., returnDate, returnDate.plusHours(1));
    }

    @Test
    public void testGetOptimizedTrip_calls() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain, forwardTripPlane));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain, backwardTripPlane));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaTrain, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaTrain, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripTrain), resultForward.getTrips());
        assertEquals(List.of(backwardTripTrain), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_calls_for_unspecifiedTransportMode() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain, forwardTripPlane));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain, backwardTripPlane));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaNotSpecified, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaNotSpecified, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripTrain), resultForward.getTrips());
        assertEquals(List.of(backwardTripTrain), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_calls_for_durationOverPrice() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain, forwardTripPlane));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain, backwardTripPlane));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaDuration, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaDuration, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripPlane), resultForward.getTrips());
        assertEquals(List.of(backwardTripPlane), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_withNotEnoughBudgetForTransport() {
        double maxPrice = 90.;
        AdditionalCriteria other = new AdditionalCriteria(departure, maxPrice, duration, "origin", "destination");

        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain));

        assertThrows(IllegalArgumentException.class, () -> {
            transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaTrain, maxPrice);
        });
    }

    @Test
    public void testGetOptimizedTrip_result_for_TrainCriteria() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain, forwardTripPlane));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain, backwardTripPlane));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaTrain, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaTrain, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripTrain), resultForward.getTrips());
        assertEquals(List.of(backwardTripTrain), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_result_for_PlaneCriteria() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripPlane, forwardTripTrain));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripPlane, backwardTripTrain));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaPlane, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaPlane, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripPlane), resultForward.getTrips());
        assertEquals(List.of(backwardTripPlane), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_noHotel_shouldReturnOnlyTransport() {
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(forwardTripTrain, forwardTripPlane));
        when(transportService.getForCity("destination", returnDate)).thenReturn(List.of(backwardTripTrain, backwardTripPlane));

        ComposedTrip resultForward = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaTrain, maxPrice);
        ComposedTrip resultBackward = transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteriaTrain, maxPrice - 100.);

        verify(transportService).getForCity("origin", departure);
        verify(transportService).getForCity("destination", returnDate);

        assertEquals(List.of(forwardTripTrain), resultForward.getTrips());
        assertEquals(List.of(backwardTripTrain), resultBackward.getTrips());
    }

    @Test
    public void testGetOptimizedTrip_withTotalPrice_equalBudget() {
        SimpleTrip trip = new SimpleTrip("origin", "destination", TransportationMode.TRAIN, maxPrice, departure, departure.plusHours(2));
        when(transportService.getForCity("origin", departure)).thenReturn(List.of(trip));

        ComposedTrip result = transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteriaTrain, maxPrice);

        assertEquals(List.of(trip), result.getTrips());

    }
}