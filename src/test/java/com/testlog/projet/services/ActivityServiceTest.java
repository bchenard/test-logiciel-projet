package com.testlog.projet.services;

import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivityServiceTest {

    private IFileReader fileReader;
    private final String activity = "{\"category\":\"Sport\",\"price\":12.5,\"name\":\"Salle de sport Rennes\",\"address\":\"10 Rue du Parc, Rennes\",\"lat\":\"48.08798165\",\"lon\":\"-1.6154534700343328\",\"days\":[true,false,true,false,true,false,false]}";

    @BeforeEach
    public void setUp() {
        fileReader = mock(IFileReader.class);
    }

    @Test
    public void testGetForCity_IOError() throws IOException {
        when(fileReader.readAll(anyString())).thenThrow(new IOException());

        Exception exception = assertThrows(RuntimeException.class, () -> new ActivityService(fileReader));
        assertTrue(exception.getMessage().contains("Failed to load city data from activities.json"), "Wrong exception message");
    }

    @Test
    public void testGetForCity_nonExistentCity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{}");
        ActivityService service = new ActivityService(fileReader);

        List<Activity> activities = service.getForCity("NoSuchCity", LocalDateTime.now());

        assertTrue(activities.isEmpty());
    }

    @Test
    public void testGetForCity_withNoActivity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[]}");
        ActivityService service = new ActivityService(fileReader);

        List<Activity> activities = service.getForCity("Rennes", LocalDateTime.now());

        assertTrue(activities.isEmpty());
    }

    @Test
    public void testGetForCity_multipleActivities() throws IOException {
        String activityB = "{\"category\":\"Music\",\"price\":25,\"name\":\"Concert de jazz\",\"address\":\"Le Liberté, Rennes\",\"lat\":\"48.10690725\",\"lon\":\"-1.6768673810895989\",\"days\":[false,false,false,false,false,true,false]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[" + activity + "," + activityB + "]}");
        ActivityService service = new ActivityService(fileReader);

        List<Activity> activities = service.getForCity("Rennes", LocalDateTime.now());

        assertEquals(2, activities.size());
        assertEquals("Salle de sport Rennes", activities.get(0).name(), "Wrong first activity name");
        assertEquals("Concert de jazz", activities.get(1).name(), "Wrong second activity name");
    }

    @Test
    public void testGetForCity_invalidLatLonFormat() throws IOException {
        String activity = "{\"category\":\"Music\",\"price\":25,\"name\":\"Concert de jazz\",\"address\":\"Le Liberté, Rennes\",\"lat\":\"invalid-lat\",\"lon\":\"invalid-lon\",\"days\":[false,false,false,false,false,true,false]}";

        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[" + activity + "]}");
        ActivityService service = new ActivityService(fileReader);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.getForCity("Rennes", LocalDateTime.now()));

        assertTrue(exception.getMessage().contains("For input string"));
    }

    @Test
    public void testGetForCity_invalidCategory() throws IOException {
        String activity = "{\"category\":\"invalid-category\",\"price\":25,\"name\":\"Concert de jazz\",\"address\":\"Le Liberté, Rennes\",\"lat\":\"invalid-lat\",\"lon\":\"invalid-lon\",\"days\":[false,false,false,false,false,true,false]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[" + activity + "]}");
        ActivityService service = new ActivityService(fileReader);

        assertThrows(IllegalArgumentException.class, () -> service.getForCity("Rennes", LocalDateTime.now()));
    }

    @Test
    public void testGetForCity_samePrice() throws IOException {
        String activityB = "{\"category\":\"Music\",\"price\":12.5,\"name\":\"Concert de jazz\",\"address\":\"Le Liberté, Rennes\",\"lat\":\"48.10690725\",\"lon\":\"-1.6768673810895989\",\"days\":[false,false,false,false,false,true,false]}";

        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[" + activity + "," + activityB + "]}");
        ActivityService service = new ActivityService(fileReader);

        List<Activity> activities = service.getForCity("Rennes", LocalDateTime.now());

        assertEquals(2, activities.size());
        assertEquals(12.5, activities.get(0).price(), "Wrong first activity price");
        assertEquals(12.5, activities.get(1).price(), "Wrong second activity price");
    }

    @Test
    public void testGetForCity_checkFields() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"Rennes\":[" + activity + "]}");

        ActivityService service = new ActivityService(fileReader);
        List<Activity> activities = service.getForCity("Rennes", LocalDateTime.now());

        assertEquals(1, activities.size());
        Activity firstActivity = activities.getFirst();
        assertEquals("Rennes", firstActivity.city(), "Wrong city");
        assertEquals(12.5, firstActivity.price(), "Wrong price");
        assertEquals("Salle de sport Rennes", firstActivity.name(), "Wrong name");
        assertEquals("10 Rue du Parc, Rennes", firstActivity.address(), "Wrong address");
        assertEquals(48.08798165, firstActivity.coordinates().lat(), 0.001, "Wrong latitude");

        List<Boolean> expectedAvailability = List.of(true, false, true, false, true, false, false);
        assertEquals(expectedAvailability, firstActivity.availability(), "Wrong availability");
    }
}