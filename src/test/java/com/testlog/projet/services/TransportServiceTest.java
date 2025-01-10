package com.testlog.projet.services;

import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.SimpleTrip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransportServiceTest {

    private final String trip = "{\"destination\":\"Destination\",\"mode\":\"train\",\"price\":12.5,\"hours\":[{\"start\":\"08:00\",\"end\":\"10:00\"}]}";
    private final LocalDateTime midnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    private IFileReader fileReader;

    @BeforeEach
    public void setUp() {
        fileReader = mock(IFileReader.class);
    }

    @Test
    public void testGetForCity_IOError() throws IOException {
        when(fileReader.readAll(anyString())).thenThrow(new IOException());

        Exception exception = assertThrows(RuntimeException.class, () -> new TransportService(fileReader));
        assertTrue(exception.getMessage().contains("Failed to load city data from trips.json"), "Wrong exception message");
    }

    @Test
    public void testGetForCity_nonExistentCity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("NoSuchCity", LocalDateTime.now());

        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_withNoTrips() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity", LocalDateTime.now());

        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_multipleTrips() throws IOException {
        String tripB = "{\"destination\":\"DestinationB\",\"mode\":\"train\",\"price\":15.0,\"hours\":[{\"start\":\"09:00\",\"end\":\"11:00\"}]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + trip + "," + tripB + "]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity", midnight);

        assertEquals("Destination", trips.get(0).arrivalCity(), "Wrong first trip destination");
        assertEquals("DestinationB", trips.get(1).arrivalCity(), "Wrong second trip destination");
        assertEquals(2, trips.size());
    }

    @Test
    public void testGetForCity_invalidTimeFormat() throws IOException {
        String trip = "{\"destination\":\"Destination\",\"mode\":\"train\",\"price\":12.5,\"hours\":[{\"start\":\"invalid-time\",\"end\":\"10:00\"}]}";

        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + trip + "]}");
        TransportService service = new TransportService(fileReader);

        assertThrows(RuntimeException.class, () -> service.getForCity("TestCity", midnight));
    }

    @Test
    public void testGetForCity_samePrice() throws IOException {
        String tripB = "{\"destination\":\"DestinationB\",\"mode\":\"train\",\"price\":12.5,\"hours\":[{\"start\":\"09:00\",\"end\":\"11:00\"}]}";

        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + trip + "," + tripB + "]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity", midnight);

        assertEquals(2, trips.size());
        assertEquals(12.5, trips.get(0).price(), "Wrong first trip price");
        assertEquals(12.5, trips.get(1).price(), "Wrong second trip price");
    }

    @Test
    public void testGetForCity_checkFields() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + trip + "]}");

        TransportService service = new TransportService(fileReader);
        List<SimpleTrip> trips = service.getForCity("TestCity", midnight);

        assertEquals(1, trips.size());
        SimpleTrip firstTrip = trips.get(0);
        assertEquals("TestCity", firstTrip.departureCity(), "Wrong departure city");
        assertEquals("Destination", firstTrip.arrivalCity(), "Wrong arrival city");
        assertEquals(12.5, firstTrip.price(), "Wrong price");
        assertEquals("TRAIN", firstTrip.mode().toString(), "Wrong mode");

        LocalDateTime expectedDepart1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
        LocalDateTime expectedArrive1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        assertEquals(expectedDepart1, firstTrip.departureTime(), "Wrong departure time");
        assertEquals(expectedArrive1, firstTrip.arrivalTime(), "Wrong arrival time");
    }
}