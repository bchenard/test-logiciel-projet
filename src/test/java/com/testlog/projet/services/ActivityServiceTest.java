package com.testlog.projet.services;

import com.testlog.projet.types.Activity;
import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.LatLng;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class ActivityServiceTest {

    @Test
    public void testGetForCity_usingMockedData() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Object activityInfo1 = createActivityInfo("Sport", 12.5, "Salle de sport Rennes", "10 Rue du Parc, Rennes", "48.08798165", "-1.6154534700343328", List.of(true, false, true, false, true, false, false));
        Object activityInfo2 = createActivityInfo("Music", 25, "Concert de jazz", "Le Liberté, Rennes", "48.10690725", "-1.6768673810895989", List.of(false, false, false, false, false, true, false));

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of(activityInfo1, activityInfo2));

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        List<Activity> activities = activityServiceSpy.getForCity("Rennes");

        assertNotNull(activities);
        assertEquals(2, activities.size());
        assertEquals("Salle de sport Rennes", activities.get(0).name());
        assertEquals("Concert de jazz", activities.get(1).name());
    }

    @Test
    public void testGetForCity_nonexistentCity() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        setPrivateField(activityServiceSpy, "cityData", Map.of());

        List<Activity> activities = activityServiceSpy.getForCity("NoSuchCity");

        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    @Test
    public void testGetForCity_emptyActivities() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of());

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        List<Activity> activities = activityServiceSpy.getForCity("Rennes");

        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    @Test
    public void testGetForCity_multipleActivities() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Object activityInfo1 = createActivityInfo("Sport", 12.5, "Salle de sport Rennes", "10 Rue du Parc, Rennes", "48.08798165", "-1.6154534700343328", List.of(true, false, true, false, true, false, false));
        Object activityInfo2 = createActivityInfo("Music", 25, "Concert de jazz", "Le Liberté, Rennes", "48.10690725", "-1.6768673810895989", List.of(false, false, false, false, false, true, false));

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of(activityInfo1, activityInfo2));

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        List<Activity> activities = activityServiceSpy.getForCity("Rennes");

        assertNotNull(activities);
        assertEquals(2, activities.size());
        assertEquals("Salle de sport Rennes", activities.get(0).name());
        assertEquals("10 Rue du Parc, Rennes", activities.get(0).address());
        assertEquals("Concert de jazz", activities.get(1).name());
        assertEquals("Le Liberté, Rennes", activities.get(1).address());
    }

    @Test
    public void testGetForCity_invalidLatLonFormat() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Object activityInfo = createActivityInfo("Sport", 12.5, "Salle de sport Rennes", "10 Rue du Parc, Rennes", "invalid-lat", "invalid-lon", List.of(true, false, true, false, true, false, false));

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of(activityInfo));

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            activityServiceSpy.getForCity("Rennes");
        });

        assertTrue(exception.getMessage().contains("For input string"));
    }

    @Test
    public void testGetForCity_invalidCategory() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Object activityInfo = createActivityInfo("InvalidCategory", 12.5, "Salle de sport Rennes", "10 Rue du Parc, Rennes", "48.08798165", "-1.6154534700343328", List.of(true, false, true, false, true, false, false));

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of(activityInfo));

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            activityServiceSpy.getForCity("Rennes");
        });

        assertTrue(exception.getMessage().contains("No enum constant"));
    }

    @Test
    public void testGetForCity_samePrice() throws Exception {
        ActivityService activityServiceSpy = spy(new ActivityService());

        Object activityInfo1 = createActivityInfo("Sport", 12.5, "Salle de sport Rennes", "10 Rue du Parc, Rennes", "48.08798165", "-1.6154534700343328", List.of(true, false, true, false, true, false, false));
        Object activityInfo2 = createActivityInfo("Music", 12.5, "Concert de jazz", "Le Liberté, Rennes", "48.10690725", "-1.6768673810895989", List.of(false, false, false, false, false, true, false));

        Map<String, List<Object>> mockCityData = Map.of("Rennes", List.of(activityInfo1, activityInfo2));

        setPrivateField(activityServiceSpy, "cityData", mockCityData);

        List<Activity> activities = activityServiceSpy.getForCity("Rennes");

        assertNotNull(activities);
        assertEquals(2, activities.size());
        assertEquals(12.5, activities.get(0).price());
        assertEquals(12.5, activities.get(1).price());
    }

    private Object createActivityInfo(String category, double price, String name, String address, String lat, String lon, List<Boolean> days) throws Exception {
        Class<?> activityInfoClass = Class.forName("com.testlog.projet.services.ActivityService$ActivityInfo");
        Constructor<?> constructor = activityInfoClass.getDeclaredConstructor();

        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }

        Object activityInfo = constructor.newInstance();

        setField(activityInfo, "category", category);
        setField(activityInfo, "price", price);
        setField(activityInfo, "name", name);
        setField(activityInfo, "address", address);
        setField(activityInfo, "lat", lat);
        setField(activityInfo, "lon", lon);
        setField(activityInfo, "days", days);

        return activityInfo;
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
        field.set(object, value);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
        field.set(object, value);
    }
}