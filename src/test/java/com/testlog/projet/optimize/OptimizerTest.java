package com.testlog.projet.optimize;

import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.CityCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.city.ICityOptimizer;
import com.testlog.projet.types.Package;
import com.testlog.projet.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class OptimizerTest {

    final LocalDateTime departure = LocalDateTime.parse("2025-01-08T08:00:00"); // Wednesday
    final Duration duration = Duration.ofDays(1);
    final LocalDateTime returnDate = departure.plus(duration);
    final List<ActivityType> activityTypes = List.of(ActivityType.CULTURE, ActivityType.MUSIC);
    final TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.TRAIN, true);
    final CityCriteria cityCriteria = new CityCriteria(1000., activityTypes, true, 3);
    final double maxPrice = 1000.;
    final AdditionalCriteria other = new AdditionalCriteria(departure, maxPrice, duration, "origin", "destination");
    ITransportOptimizer transportOptimizer;
    ICityOptimizer cityOptimizer;
    Optimizer optimizer;

    @BeforeEach
    public void setUp() {
        transportOptimizer = mock(ITransportOptimizer.class);
        cityOptimizer = mock(ICityOptimizer.class);
        optimizer = new Optimizer(transportOptimizer, cityOptimizer);
    }

    @Test
    public void testSolve_calls() {
        Pair<Hotel, List<Activity>> cityTrip = new Pair<>(null, Collections.emptyList());
        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        when(cityOptimizer.optimize(anyString(), anyInt(), anyInt(), anyDouble(), any(), any())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        optimizer.solve(transportCriteria, cityCriteria, other);

        verify(transportOptimizer).getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice);
        verify(transportOptimizer).getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.);
        verify(cityOptimizer).optimize(anyString(), anyInt(), anyInt(), anyDouble(), any(), any());
        verify(cityOptimizer).getTotalPrice(cityTrip, 1);
    }

    @Test
    public void testSolve_withMultipleDays() {
        Duration duration = Duration.ofDays(2);
        LocalDateTime returnDate = departure.plus(duration);
        AdditionalCriteria other = new AdditionalCriteria(departure, 1000., duration, "origin", "destination");

        Pair<Hotel, List<Activity>> cityTrip = new Pair<>(null, Collections.emptyList());
        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        when(cityOptimizer.optimize(anyString(), anyInt(), anyInt(), anyDouble(), any(), any())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        optimizer.solve(transportCriteria, cityCriteria, other);

        verify(transportOptimizer).getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice);
        verify(transportOptimizer).getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.);
        verify(cityOptimizer).optimize(anyString(), anyInt(), anyInt(), anyDouble(), any(), any());
        verify(cityOptimizer).getTotalPrice(cityTrip, 2);
    }

    @Test
    public void testSolve_withNotEnoughBudgetForTransport() {
        double maxPrice = 100.;
        AdditionalCriteria other = new AdditionalCriteria(departure, maxPrice, duration, "origin", "destination");

        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        Package result = optimizer.solve(transportCriteria, cityCriteria, other);

        assertNull(result.activities());
        assertNull(result.hotel());
        assertEquals(forward, result.firstTrip());
        assertEquals(backward, result.returnTrip());
        assertEquals(300., result.totalPrice());
    }

    @Test
    public void testSolve_result() {
        Pair<Hotel, List<Activity>> cityTrip = new Pair<>(null, Collections.emptyList());
        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        when(cityOptimizer.optimize(anyString(), anyInt(), anyInt(), anyDouble(), any(), any())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        Package result = optimizer.solve(transportCriteria, cityCriteria, other);

        assertEquals(0, result.activities().size());
        assertNull(result.hotel());
        assertEquals(forward, result.firstTrip());
        assertEquals(backward, result.returnTrip());
        assertEquals(350., result.totalPrice());
    }

    @Test
    public void testSolve_checkOptimizeCall() {
        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        CityCriteria cityCriteria = new CityCriteria(1000., activityTypes, false, 3);

        Hotel h = new Hotel("hotel", new LatLng(0., 0.), 3, "hotel A", 100., "address");
        when(cityOptimizer.optimize("destination", 2, 1, 700., cityCriteria, departure)).thenReturn(new Pair<>(h, List.of()));

        optimizer.solve(transportCriteria, cityCriteria, other);

        verify(cityOptimizer).optimize("destination", 2, 1, 700., cityCriteria, departure);
    }
}