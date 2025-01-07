package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.services.ActivityService;
import com.testlog.projet.services.HotelService;
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
import static org.mockito.Mockito.when;

public class CityOptimizerTest {

    @Test
    public void runTest() {
        HotelService hotelService = new HotelService();
        ActivityService activityService = new ActivityService();
        CitySolver citySolver = new CitySolver();
        CityOptimizer cityOptimizer = new CityOptimizer(hotelService, activityService, citySolver);
        int startDay = 0;
        int nbDays = 2;
        double budget = 99999;

        HotelCriteria hotelCriteria = new HotelCriteria(true, 3);
        List<ActivityType> types = List.of(ActivityType.MUSIC, ActivityType.CINEMA, ActivityType.CULTURE, ActivityType.SPORT);
        ActivityCriteria activityCriteria = new ActivityCriteria(300, types);

        cityOptimizer.optimize("Bordeaux", startDay, nbDays, budget, hotelCriteria, activityCriteria);
    }

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
    final Hotel hotelA = new Hotel("Bordeaux", new LatLng(1., 1.), 3, "Hotel1", 100);
    final Hotel hotelB = new Hotel("Bordeaux", new LatLng(1., 1.), 4, "Hotel2", 200);

    final Activity activityA = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));
    final Activity activityB = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 75., List.of(true, true, true, true, true, true, true));

    final Pair<Hotel, List<Activity>> pairA = new Pair<>(hotelA, List.of(activityA, activityB));
    final Pair<Hotel, List<Activity>> pairB = new Pair<>(hotelB, List.of(activityA, activityB));

    @Test
    public void testGetTotalPrice_withoutElements() {
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, List.of());

        assertEquals(100, cityOptimizer.getTotalPrice(pair));
    }

    @Test
    public void testGetTotalPrice_withNullElements() {
        List<Activity> activities = Arrays.asList(null, null, null);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(100, cityOptimizer.getTotalPrice(pair));
    }

    @Test
    public void testGetTotalPrice_withOneElement() {
        List<Activity> activities = List.of(activityA);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(150, cityOptimizer.getTotalPrice(pair));
    }

    @Test
    public void testGetTotalPrice_withMultipleNonNullElements() {
        List<Activity> activities = Arrays.asList(null, null, activityA, null, activityB, null, null);
        Pair<Hotel, List<Activity>> pair = new Pair<>(hotelA, activities);

        assertEquals(225, cityOptimizer.getTotalPrice(pair));
    }

    @Test
    public void testCompare_withLessActivities() {
        Pair<Hotel, List<Activity>> a = new Pair<>(hotelA, List.of(activityA, activityB));
        Pair<Hotel, List<Activity>> b = new Pair<>(hotelB, List.of(activityA));

        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertTrue(cityOptimizer.compare(a, b, hotelCriteria));
    }

    @Test
    public void testCompare_withMoreActivities() {
        Pair<Hotel, List<Activity>> a = new Pair<>(hotelA, List.of(activityA));
        Pair<Hotel, List<Activity>> b = new Pair<>(hotelB, List.of(activityA, activityB));

        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertFalse(cityOptimizer.compare(a, b, hotelCriteria));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);
        assertTrue(cityOptimizer.compare(pairA, pairB, hotelCriteria));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMinPrice() {
        HotelCriteria hotelCriteria = new HotelCriteria(true, 0);
        assertFalse(cityOptimizer.compare(pairB, pairA, hotelCriteria));
    }

    @Test
    public void testCompare_withSameActivities_lowerPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertFalse(cityOptimizer.compare(pairA, pairB, hotelCriteria));
    }

    @Test
    public void testCompare_withSameActivities_higherPrice_preferMaxStars() {
        HotelCriteria hotelCriteria = new HotelCriteria(false, 0);
        assertTrue(cityOptimizer.compare(pairB, pairA, hotelCriteria));
    }

    @Test
    public void testOptimize_withNoActivities() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of());
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(null, null, null));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));

        assertEquals(hotelA, result.first());
        assertEquals(3, result.second().size(), "3 days should be planned, eventually null");

        assertNull(result.second().get(0), "No activity should be planned");
        assertNull(result.second().get(1), "No activity should be planned");
        assertNull(result.second().get(2), "No activity should be planned");

    }

    @Test
    public void testOptimize_withNoHotels() {
        when(hotelService.getForCity(any())).thenReturn(List.of());
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(null, null, null));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));

        assertNull(result);
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_noCategory() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(null, null, null));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of()));

        assertEquals(hotelA, result.first());
        assertEquals(3, result.second().size(), "No activity should be planned");
    }

    @Test
    public void testOptimize_withOneHotelAndOneActivity_withItsCategory() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, null, null));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE)));

        assertEquals(hotelA, result.first());
        assertEquals(3, result.second().size(), "3 days should be planned, eventually null");
        assertEquals(activityA, result.second().getFirst(), "The activity should be planned");
        assertNull(result.second().get(1), "No activity should be planned");
        assertNull(result.second().get(2), "No activity should be planned");
    }

    @Test
    public void testOptimize_withMultipleHotelsAndActivities() {
        when(hotelService.getForCity(any())).thenReturn(List.of(hotelA, hotelB));
        when(activityService.getForCity(any())).thenReturn(List.of(activityA, activityB));
        when(citySolver.solve(any(), anyInt(), anyInt(), anyDouble())).thenReturn(Arrays.asList(activityA, activityB, null));

        Pair<Hotel, List<Activity>> result = cityOptimizer.optimize("Bordeaux", 0, 2, 99999, new HotelCriteria(true, 3), new ActivityCriteria(300, List.of(ActivityType.CULTURE, ActivityType.CINEMA)));

        assertEquals(hotelA, result.first());
        assertEquals(3, result.second().size(), "3 days should be planned");
        assertEquals(activityA, result.second().getFirst(), "The activity should be planned");
        assertEquals(activityB, result.second().get(1), "The activity should be planned");
        assertNull(result.second().get(2), "No activity should be planned");
    }
}