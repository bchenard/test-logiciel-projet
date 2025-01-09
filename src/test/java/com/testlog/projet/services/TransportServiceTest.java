package com.testlog.projet.services;

import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TransportServiceTest {

    private IFileReader fileReader;

    @BeforeEach
    public void setUp() {
        fileReader = mock(IFileReader.class);
    }

    // IOException added ONLY for when(fileReader.readAll()). This is required for the test to execute.
    @Test
    public void testGetForCity_IOError() throws IOException {
        when(fileReader.readAll(anyString())).thenThrow(new IOException());

        Exception exception = assertThrows(RuntimeException.class, () -> new TransportService(fileReader));
        assertTrue(exception.getMessage().contains("Failed to load city data from trips.json"), "Wrong exception message");
    }

    @Test
    public void testGetForCity_withOneCity_andOneDirection_checkFields() throws IOException {
        String schedules = "{\"destination\":\"Destination\",\"mode\":\"train\",\"price\":12.5,\"hours\":[{\"start\":\"08:00\",\"end\":\"10:00\"},{\"start\":\"14:30\",\"end\":\"15:30\"}]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + schedules + "]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity");

        assertFalse(trips.isEmpty());
        assertEquals(2, trips.size());

        SimpleTrip first = trips.get(0);
        assertEquals("TestCity", first.departureCity());
        assertEquals("Destination", first.arrivalCity());
        assertEquals(TransportationMode.TRAIN, first.mode());
        assertEquals(12.5, first.price(), 0.001);

        LocalDateTime expectedDepart1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
        LocalDateTime expectedArrive1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        assertEquals(expectedDepart1, first.departureTime());
        assertEquals(expectedArrive1, first.arrivalTime());

        SimpleTrip second = trips.get(1);
        LocalDateTime expectedDepart2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 30));
        LocalDateTime expectedArrive2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 30));
        assertEquals(expectedDepart2, second.departureTime());
        assertEquals(expectedArrive2, second.arrivalTime());
    }

    @Test
    public void testGetForCity_nonexistentCity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("NoSuchCity");

        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_emptySchedule() throws IOException {
        String schedules = "{\"destination\":\"Destination\",\"mode\":\"train\",\"price\":5.0,\"hours\":[]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[ " + schedules + " ]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity");

        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_multipleConnections() throws IOException {
        String schedules1 = "{\"destination\":\"Destination1\",\"mode\":\"train\",\"price\":10.0,\"hours\":[{\"start\":\"08:00\",\"end\":\"09:00\"}]}";
        String schedules2 = "{\"destination\":\"Destination2\",\"mode\":\"train\",\"price\":3.5,\"hours\":[{\"start\":\"10:00\",\"end\":\"11:00\"}]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + schedules1 + "," + schedules2 + "]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity");

        assertEquals(2, trips.size());

        SimpleTrip first = trips.getFirst();
        assertEquals("Destination1", first.arrivalCity());
        assertEquals(TransportationMode.TRAIN, first.mode());
    }

    @Test
    public void testGetForCity_invalidTimeFormat() throws IOException {
        String schedules = "{\"destination\":\"Destination\",\"mode\":\"train\",\"price\":12.5,\"hours\":[{\"start\":\"invalid-time\",\"end\":\"10:00\"}]}";
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[" + schedules + "]}");
        TransportService service = new TransportService(fileReader);

        assertThrows(RuntimeException.class, () -> service.getForCity("TestCity"));
    }

    @Test
    public void testGetForCity_noConnectionsForCity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"TestCity\":[]}");
        TransportService service = new TransportService(fileReader);

        List<SimpleTrip> trips = service.getForCity("TestCity");

        assertTrue(trips.isEmpty());
    }
}