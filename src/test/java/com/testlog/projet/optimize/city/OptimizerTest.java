package com.testlog.projet.optimize.city;

import com.testlog.projet.types.ComposedTrip;
import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.ITransportOptimizer;
import com.testlog.projet.optimize.Optimizer;
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

    ITransportOptimizer transportOptimizer;
    ICityOptimizer cityOptimizer;
    Optimizer optimizer;

    @BeforeEach
    public void setUp() {
        transportOptimizer = mock(ITransportOptimizer.class);
        cityOptimizer = mock(ICityOptimizer.class);
        optimizer = new Optimizer(transportOptimizer, cityOptimizer);
    }

    final LocalDateTime departure = LocalDateTime.parse("2025-01-08T08:00:00"); // Wednesday
    final Duration duration = Duration.ofDays(1);
    final LocalDateTime returnDate = departure.plus(duration);
    final List<ActivityType> activityTypes = List.of(ActivityType.CULTURE, ActivityType.MUSIC);
    final TransportCriteria transportCriteria = new TransportCriteria(TransportationMode.TRAIN, true);
    final HotelCriteria hotelCriteria = new HotelCriteria(true, 3);
    final ActivityCriteria activityCriteria = new ActivityCriteria(1000., activityTypes);
    final double maxPrice = 1000.;
    final AdditionalCriteria other = new AdditionalCriteria(departure, maxPrice, duration, "origin", "destination");

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

        when(cityOptimizer.optimize("destination", 2, 1, 700., hotelCriteria, activityCriteria, any())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, other);

        verify(transportOptimizer).getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice);
        verify(transportOptimizer).getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.);
        verify(cityOptimizer).optimize("destination", 2, 1, 700., hotelCriteria, activityCriteria, any());
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

        when(cityOptimizer.optimize("destination", 2, 2, 700., hotelCriteria, activityCriteria, LocalDateTime.now())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, other);

        verify(transportOptimizer).getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice);
        verify(transportOptimizer).getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.);
        verify(cityOptimizer).optimize("destination", 2, 2, 700., hotelCriteria, activityCriteria, any());
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

        Package result = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, other);

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

        when(cityOptimizer.optimize("destination", 2, 1, 700., hotelCriteria, activityCriteria, any())).thenReturn(cityTrip);
        when(cityOptimizer.getTotalPrice(any(), anyInt())).thenReturn(50.);

        Package result = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, other);

        assertEquals(0, result.activities().size());
        assertNull(result.hotel());
        assertEquals(forward, result.firstTrip());
        assertEquals(backward, result.returnTrip());
        assertEquals(350., result.totalPrice());
    }

    @Test
    public void testSolve_noHotel_shouldReturnOnlyTransport() {
        ComposedTrip forward = mock(ComposedTrip.class);
        ComposedTrip backward = mock(ComposedTrip.class);

        when(transportOptimizer.getOptimizedTrip("origin", "destination", departure, transportCriteria, maxPrice)).thenReturn(forward);
        when(transportOptimizer.getOptimizedTrip("destination", "origin", returnDate, transportCriteria, maxPrice - 100.)).thenReturn(backward);

        when(forward.getArrivalTime()).thenReturn(departure);
        when(backward.getDepartureTime()).thenReturn(returnDate);
        when(forward.getPrice()).thenReturn(100.);
        when(backward.getPrice()).thenReturn(200.);

        when(cityOptimizer.optimize("destination", 2, 1, 700., hotelCriteria, activityCriteria, any())).thenReturn(null);

        Package result = optimizer.solve(transportCriteria, hotelCriteria, activityCriteria, other);

        assertNull(result.activities());
        assertNull(result.hotel());
        assertEquals(forward, result.firstTrip());
        assertEquals(backward, result.returnTrip());
        assertEquals(300., result.totalPrice());
    }
}
