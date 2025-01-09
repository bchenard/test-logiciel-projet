package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ICityService;
import com.testlog.projet.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CityOptimizerTest {
    @Mock
    ICityService<Hotel> hotelService;
    @Mock
    ICityService<Activity> activityService;
    @Mock
    ICitySolver citySolver;

    CityOptimizer cityOptimizer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);
    }

    // Hotel B has more stars than Hotel A, but Hotel A has a lower price
    final Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel1", 100, "Address A");
    final Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 4, "Hotel2", 200, "Address B");

    final Activity activityA = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));
    final Activity activityB = new Activity("name", "address", "Bordeaux", new LatLng(1.3, 1.3), ActivityType.CINEMA, 75., List.of(true, true, true, true, true, true, true));

    // Distance between hotel A and activity B is 47.171

    final Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
    final Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

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

        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertTrue(cityOptimizer.compare(a, b, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withMoreActivities() {
        Pair<Hotel, List<Activity>> a = new Pair<>(hotelA, List.of(activityA));
        Pair<Hotel, List<Activity>> b = new Pair<>(hotelB, List.of(activityA, activityB));

        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertFalse(cityOptimizer.compare(a, b, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);
        assertTrue(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);
        assertFalse(cityOptimizer.compare(pairB, pairA, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertFalse(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertTrue(cityOptimizer.compare(pairB, pairA, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalPrice_lowerStars_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);

        // Hotel B has lower stars than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertFalse(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalPrice_higherStars_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);

        // Hotel B has higher stars than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertTrue(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalStars_lowerPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);

        // Hotel B is more expensive than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 100, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 101, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertTrue(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withSameActivities_equalStars_higherPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);

        // Hotel B is less expensive than Hotel A
        Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel1", 101, "Address A");
        Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 2, "Hotel2", 100, "Address B");

        Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

        assertFalse(cityOptimizer.compare(pairA, pairB, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withEqualObjects_shouldReturnFirst_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertTrue(cityOptimizer.compare(pairA, pairA, hotelCriteria, 1));
    }

    @Test
    public void testCompare_withNullSecond_shouldReturnFirst_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);
        assertTrue(cityOptimizer.compare(pairA, pairA, hotelCriteria, 1));
    }


    @Test
    public void testOptimize_withNoActivities() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of());

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withNoHotels() {
        when(hotelService.getForCity(any())).thenReturn(List.of());
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(null, null, null));

        assertThrows(IllegalArgumentException.class, () -> {
            cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));
        });
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_noCategory() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_withItsCategory() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE)));

        verify(citySolver).solve(List.of(activityA), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withMultipleHotelsAndActivities() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));

        verify(citySolver).solve(List.of(activityA, activityB), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_withTooHighStarsRequired() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));

        assertThrows(IllegalArgumentException.class, () -> {
            cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 5), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));
        });
    }

    @Test
    public void testOptimize_withTooFarAwayActivity() {
        Activity farActivity = new Activity("name", "address", "Bordeaux", new LatLng(10., 10.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));

        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(farActivity));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE)));

        verify(citySolver).solve(List.of(), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_checkSolveInput() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB, null));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));

        verify(citySolver).solve(List.of(activityA, activityB), 0, 2, 99999 - 100 * 2);
    }

    @Test
    public void testOptimize_checkSolveInput_withDifferentHotel() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelB));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB, null));

        cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CINEMA)));

        verify(citySolver).solve(List.of(activityB), 0, 2, 99999 - 200 * 2);
    }

    @Test
    public void testOptimize_returnValue_whenOptimal() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));

        assertEquals(hotelA, result.first());
        assertEquals(2, result.second().size());
        assertEquals(List.of(activityA, activityB), result.second());
    }

    @Test
    public void testOptimize_returnValue_whenOptimal_withDifferentHotel() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(false, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));

        assertEquals(hotelB, result.first());
        assertEquals(2, result.second().size());
        assertEquals(List.of(activityA, activityB), result.second());
    }
}