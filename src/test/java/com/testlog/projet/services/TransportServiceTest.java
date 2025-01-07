package com.testlog.projet.services;

import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class TransportServiceTest {

    @Test
    public void testGetForCity_usingMockedData() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        Object schedule1 = createSchedule("08:00", "10:00");
        Object schedule2 = createSchedule("14:30", "15:30");

        Object connectionInfo = createConnectionInfo(
                "MockDestination",
                "TRAIN",
                12.5,
                List.of(schedule1, schedule2)
        );

        Map<String, List<Object>> mockCityData = Map.of("TestCity", List.of(connectionInfo));

        setPrivateField(transportServiceSpy, "cityData", mockCityData);

        List<SimpleTrip> trips = transportServiceSpy.getForCity("TestCity");

        assertNotNull(trips);
        assertFalse(trips.isEmpty());
        assertEquals(2, trips.size());

        SimpleTrip first = trips.get(0);
        assertEquals("TestCity", first.departureCity());
        assertEquals("MockDestination", first.arrivalCity());
        assertEquals(TransportationMode.TRAIN, first.mode());
        assertEquals(12.5, first.price(), 0.001);

        Instant expectedDepart1 = toInstant(LocalDate.now(), LocalTime.of(8, 0));
        Instant expectedArrive1 = toInstant(LocalDate.now(), LocalTime.of(10, 0));
        assertEquals(expectedDepart1, first.departureTime());
        assertEquals(expectedArrive1, first.arrivalTime());

        SimpleTrip second = trips.get(1);
        Instant expectedDepart2 = toInstant(LocalDate.now(), LocalTime.of(14, 30));
        Instant expectedArrive2 = toInstant(LocalDate.now(), LocalTime.of(15, 30));
        assertEquals(expectedDepart2, second.departureTime());
        assertEquals(expectedArrive2, second.arrivalTime());
    }

    @Test
    public void testGetForCity_nonexistentCity() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        setPrivateField(transportServiceSpy, "cityData", Map.of());

        List<SimpleTrip> trips = transportServiceSpy.getForCity("NoSuchCity");

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_emptySchedule() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        Object connectionInfo = createConnectionInfo(
                "MockDestination",
                "BUS",
                5.0,
                List.of()
        );

        Map<String, List<Object>> mockCityData = Map.of("TestCity", List.of(connectionInfo));

        setPrivateField(transportServiceSpy, "cityData", mockCityData);

        List<SimpleTrip> trips = transportServiceSpy.getForCity("TestCity");

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }

    @Test
    public void testGetForCity_multipleConnections() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        Object schedule1 = createSchedule("08:00", "09:00");
        Object schedule2 = createSchedule("10:00", "11:00");
        Object connection1 = createConnectionInfo(
                "Destination1",
                "TRAIN",
                10.0,
                List.of(schedule1)
        );
        Object connection2 = createConnectionInfo(
                "Destination2",
                "TRAIN",
                3.5,
                List.of(schedule2)
        );

        Map<String, List<Object>> mockCityData = Map.of("TestCity", List.of(connection1, connection2));

        setPrivateField(transportServiceSpy, "cityData", mockCityData);

        List<SimpleTrip> trips = transportServiceSpy.getForCity("TestCity");

        assertNotNull(trips);
        assertEquals(2, trips.size());

        SimpleTrip first = trips.get(0);
        assertEquals("Destination1", first.arrivalCity());
        assertEquals(TransportationMode.TRAIN, first.mode());
    }

    @Test
    public void testGetForCity_invalidTimeFormat() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        Object schedule = createSchedule("invalid-time", "10:00");

        Object connectionInfo = createConnectionInfo(
                "MockDestination",
                "TRAIN",
                12.5,
                List.of(schedule)
        );

        Map<String, List<Object>> mockCityData = Map.of("TestCity", List.of(connectionInfo));

        setPrivateField(transportServiceSpy, "cityData", mockCityData);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transportServiceSpy.getForCity("TestCity");
        });

        assertTrue(exception.getMessage().contains("For input string"));
    }

    @Test
    public void testGetForCity_noConnectionsForCity() throws Exception {
        TransportService transportServiceSpy = spy(new TransportService());

        Map<String, List<Object>> mockCityData = Map.of("AnotherCity", List.of());

        setPrivateField(transportServiceSpy, "cityData", mockCityData);

        List<SimpleTrip> trips = transportServiceSpy.getForCity("TestCity");

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }

    private Instant toInstant(LocalDate date, LocalTime time) {
        return time.atDate(date).atZone(ZoneId.systemDefault()).toInstant();
    }

    private Object createSchedule(String start, String end) throws Exception {
        Class<?> scheduleClass = Class.forName("com.testlog.projet.services.TransportService$Schedule");
        Constructor<?> constructor = scheduleClass.getDeclaredConstructor();

        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }

        Object schedule = constructor.newInstance();

        Field startField = scheduleClass.getDeclaredField("start");
        if (!startField.canAccess(schedule)) {
            startField.setAccessible(true);
        }
        startField.set(schedule, start);

        Field endField = scheduleClass.getDeclaredField("end");
        if (!endField.canAccess(schedule)) {
            endField.setAccessible(true);
        }
        endField.set(schedule, end);

        return schedule;
    }

    private Object createConnectionInfo(String destination, String mode, double price, List<Object> hours) throws Exception {
        Class<?> connectionInfoClass = Class.forName("com.testlog.projet.services.TransportService$ConnectionInfo");
        Constructor<?> constructor = connectionInfoClass.getDeclaredConstructor();

        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }

        Object connectionInfo = constructor.newInstance();

        Field destinationField = connectionInfoClass.getDeclaredField("destination");
        if (!destinationField.canAccess(connectionInfo)) {
            destinationField.setAccessible(true);
        }
        destinationField.set(connectionInfo, destination);

        Field modeField = connectionInfoClass.getDeclaredField("mode");
        if (!modeField.canAccess(connectionInfo)) {
            modeField.setAccessible(true);
        }
        modeField.set(connectionInfo, mode);

        Field priceField = connectionInfoClass.getDeclaredField("price");
        if (!priceField.canAccess(connectionInfo)) {
            priceField.setAccessible(true);
        }
        priceField.set(connectionInfo, price);

        Field hoursField = connectionInfoClass.getDeclaredField("hours");
        if (!hoursField.canAccess(connectionInfo)) {
            hoursField.setAccessible(true);
        }
        hoursField.set(connectionInfo, hours);

        return connectionInfo;
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
        field.set(object, value);
    }
}