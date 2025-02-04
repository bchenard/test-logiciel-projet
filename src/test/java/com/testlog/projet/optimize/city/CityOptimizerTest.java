package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.CityCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CityOptimizerTest {
    // Hotel B has more stars than Hotel A, but Hotel A has a lower price
    final Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel1", 100, "Address A");
    final Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 4, "Hotel2", 200, "Address B");
    final Activity activityA = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));
    final Activity activityB = new Activity("name", "address", "Bordeaux", new LatLng(1.3, 1.3), ActivityType.CINEMA, 75., List.of(true, true, true, true, true, true, true));
    final Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
    final Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));
    @Mock
    ICityService<Hotel> hotelService;
    @Mock
    ICityService<Activity> activityService;
    @Mock
    ICitySolver citySolver;

    // Distance between hotel A and activity B is 47.171
    CityOptimizer cityOptimizer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);
    }

    @Test
    public void testGetTotalPrice_withoutElements() {
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, List.of());

        assertEquals(100, cityOptimizer.getTotalPrice(pair, 1));
    }

    @Test
    public void testGetTotalPrice_withNullElements() {
        List<Activity> activities = Arrays.asList(null, null, null);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(100, cityOptimizer.getTotalPrice(pair, 1));
    }

    @Test
    public void testGetTotalPrice_withOneElement() {
        List<Activity> activities = List.of(activityA);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(150, cityOptimizer.getTotalPrice(pair, 1));
    }

    @Test
    public void testGetTotalPrice_withMultipleNonNullElements() {
        List<Activity> activities = Arrays.asList(null, null, activityA, null, activityB, null, null);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(225, cityOptimizer.getTotalPrice(pair, 1));
    }

    @Test
    public void testGetTotalPrice_withMultipleDays() {
        List<Activity> activities = Arrays.asList(null, null, activityA, null, activityB, null, null);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(325, cityOptimizer.getTotalPrice(pair, 2));
    }

    @Test
    public void testCompare_withLessActivities() {
        Pair<Hotel, List<Activity>> a = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> b = new Pair<>(hotelB, List.of(activityA));

        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);
        assertTrue(cityOptimizer.compare(a, b, criteria, 1));
    }

    @Test
    public void testCompare_withMoreActivities() {
        Pair<Hotel, List<Activity>> a = new Pair<>(hotelA, List.of(activityA));
        Pair<Hotel, List<Activity>> b = new Pair<>(hotelB, List.of(activityA, activityB));

        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);
        assertFalse(cityOptimizer.compare(a, b, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMinPrice() {
        CityCriteria criteria = new CityCriteria(0, List.of(), true, 0);
        assertTrue(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMinPrice() {
        CityCriteria criteria = new CityCriteria(0, List.of(), true, 0);
        assertFalse(cityOptimizer.compare(pairB, pairA, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMaxStars() {
        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);
        assertFalse(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMaxStars() {
        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);
        assertTrue(cityOptimizer.compare(pairB, pairA, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalPrice_lowerStars_preferMinPrice() {
        CityCriteria criteria = new CityCriteria(0, List.of(), true, 0);

        // Hotel B has lower stars than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertFalse(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalPrice_higherStars_preferMinPrice() {
        CityCriteria criteria = new CityCriteria(0, List.of(), true, 0);

        // Hotel B has higher stars than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertTrue(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalStars_lowerPrice_preferMaxStars() {
        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);

        // Hotel B is more expensive than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 101, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertTrue(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalStars_higherPrice_preferMaxStars() {
        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);

        // Hotel B is less expensive than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 101, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertFalse(cityOptimizer.compare(pairA, pairB, criteria, 1));
    }

    @Test
    public void testCompare_withEqualObjects_shouldReturnFirst_preferMinPrice() {
        CityCriteria criteria = new CityCriteria(0, List.of(), false, 0);
        assertTrue(cityOptimizer.compare(pairA, pairA, criteria, 1));
    }

    @Test
    public void testCompare_withNullSecond_shouldReturnFirst_preferMaxStars() {
        CityCriteria criteria = new CityCriteria(0, List.of(), true, 0);
        assertTrue(cityOptimizer.compare(pairA, pairA, criteria, 1));
    }


    @Test
    public void testOptimize_withNoActivities() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of());

        CityCriteria criteria = new CityCriteria(300, List.of(), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withNoHotels() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of());
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(null, null, null));

        CityCriteria criteria = new CityCriteria(300, List.of(), true, 3);

        assertThrows(IllegalArgumentException.class, () -> {
            cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());
        });
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_noCategory() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA));

        CityCriteria criteria = new CityCriteria(300, List.of(), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_withItsCategory() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(activityA), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withMultipleHotelsAndActivities() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(activityA, activityB), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withTooHighStarsRequired() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());
        });
    }

    @Test
    public void testOptimize_withTooFarAwayActivity() {
        Activity farActivity = new Activity("name", "address", "Bordeaux", new LatLng(10., 10.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));

        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(farActivity));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_checkSolveInput() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB, null));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(activityA, activityB), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_checkSolveInput_withDifferentHotel() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelB));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB, null));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CINEMA), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        verify(citySolver).solve(List.of(activityB), 0, 2, 99999 - 200 * 2);
    }

    @Test
    public void testOptimize_returnValue_whenOptimal() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 3);

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        assertEquals(hotelA, result.first());
        assertEquals(2, result.second().size());
        assertEquals(List.of(activityA, activityB), result.second());
    }

    @Test
    public void testOptimize_returnValue_whenOptimal_withDifferentHotel() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB));

        CityCriteria criteria = new CityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA), false, 3);

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, criteria, LocalDateTime.now());

        assertEquals(hotelB, result.first());
        assertEquals(2, result.second().size());
        assertEquals(List.of(activityA, activityB), result.second());
    }

    @Test
    public void testOptimize_withSameBudget_asHotel() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));

        CityCriteria criteria = new CityCriteria(200, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 3);

        cityOptimizer.optimize("Bordeaux", 0, 2, 200, criteria, LocalDateTime.now());

        verify(citySolver, times(1)).solve(any(), anyInt(), anyInt(), anyDouble());
    }

    @Test
    public void testOptimize_withTooLowBudget() {
        when(hotelService.getForCity(any(), any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any(), any())).thenReturn(List.of(activityA, activityB));

        CityCriteria criteria = new CityCriteria(200, List.of(ActivityType.CULTURE, ActivityType.CINEMA), true, 3);

        assertThrows(IllegalArgumentException.class, () -> cityOptimizer.optimize("Bordeaux", 0, 2, 199, criteria, LocalDateTime.now()));

        verify(citySolver, never()).solve(any(), anyInt(), anyInt(), anyDouble());
    }
}