package com.testlog.projet.services;

import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class HotelServiceTest {

    @Test
    public void testGetForCity_withValidData() throws Exception {
        HotelService hotelServiceSpy = spy(new HotelService());

        Object hotelInfo1 = createHotelInfo("Hotel A", 4, 150.0, "Address A", "48.8566", "2.3522");
        Object hotelInfo2 = createHotelInfo("Hotel B", 5, 300.0, "Address B", "48.8584", "2.2945");

        Map<String, List<Object>> mockCityData = Map.of("Paris", List.of(hotelInfo1, hotelInfo2));
        setPrivateField(hotelServiceSpy, "cityData", mockCityData);

        List<Hotel> hotels = hotelServiceSpy.getForCity("Paris");

        assertNotNull(hotels);
        assertEquals(2, hotels.size());

        Hotel firstHotel = hotels.get(0);
        assertEquals("Paris", firstHotel.city());
        assertEquals("Hotel A", firstHotel.name());
        assertEquals(4, firstHotel.stars());
        assertEquals(150.0, firstHotel.price(), 0.001);
        assertEquals(new LatLng(48.8566, 2.3522), firstHotel.coordinates());

        Hotel secondHotel = hotels.get(1);
        assertEquals("Paris", secondHotel.city());
        assertEquals("Hotel B", secondHotel.name());
        assertEquals(5, secondHotel.stars());
        assertEquals(300.0, secondHotel.price(), 0.001);
        assertEquals(new LatLng(48.8584, 2.2945), secondHotel.coordinates());
    }

    @Test
    public void testGetForCity_nonexistentCity() throws Exception {
        HotelService hotelServiceSpy = spy(new HotelService());

        setPrivateField(hotelServiceSpy, "cityData", Map.of());

        List<Hotel> hotels = hotelServiceSpy.getForCity("NonexistentCity");

        assertNotNull(hotels);
        assertTrue(hotels.isEmpty());
    }

    @Test
    public void testGetForCity_withEmptyData() throws Exception {
        HotelService hotelServiceSpy = spy(new HotelService());

        Map<String, List<Object>> mockCityData = Map.of("Paris", List.of());
        setPrivateField(hotelServiceSpy, "cityData", mockCityData);

        List<Hotel> hotels = hotelServiceSpy.getForCity("Paris");

        assertNotNull(hotels);
        assertTrue(hotels.isEmpty());
    }

    @Test
    public void testGetForCity_invalidLatLon() throws Exception {
        HotelService hotelServiceSpy = spy(new HotelService());

        Object hotelInfo = createHotelInfo("Invalid Hotel", 3, 100.0, "Invalid Address", "invalid-lat", "invalid-lon");

        Map<String, List<Object>> mockCityData = Map.of("Paris", List.of(hotelInfo));
        setPrivateField(hotelServiceSpy, "cityData", mockCityData);

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            hotelServiceSpy.getForCity("Paris");
        });

        assertTrue(exception.getMessage().contains("For input string"));
    }

    @Test
    public void testGetForCity_multipleCities() throws Exception {
        HotelService hotelServiceSpy = spy(new HotelService());

        Object hotelInfo1 = createHotelInfo("Hotel A", 4, 150.0, "Address A", "48.8566", "2.3522");
        Object hotelInfo2 = createHotelInfo("Hotel B", 5, 300.0, "Address B", "48.8584", "2.2945");
        Object hotelInfo3 = createHotelInfo("Hotel C", 3, 80.0, "Address C", "48.8606", "2.3376");

        Map<String, List<Object>> mockCityData = Map.of(
                "Paris", List.of(hotelInfo1, hotelInfo2),
                "London", List.of(hotelInfo3)
        );
        setPrivateField(hotelServiceSpy, "cityData", mockCityData);

        List<Hotel> parisHotels = hotelServiceSpy.getForCity("Paris");
        List<Hotel> londonHotels = hotelServiceSpy.getForCity("London");

        assertNotNull(parisHotels);
        assertEquals(2, parisHotels.size());

        assertNotNull(londonHotels);
        assertEquals(1, londonHotels.size());
        assertEquals("Hotel C", londonHotels.get(0).name());
    }

    private Object createHotelInfo(String name, int stars, double price, String address, String lat, String lon) throws Exception {
        Class<?> hotelInfoClass = Class.forName("com.testlog.projet.services.HotelService$HotelInfo");
        Constructor<?> constructor = hotelInfoClass.getDeclaredConstructor();

        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }

        Object hotelInfo = constructor.newInstance();

        setField(hotelInfo, "name", name);
        setField(hotelInfo, "stars", stars);
        setField(hotelInfo, "price", price);
        setField(hotelInfo, "address", address);
        setField(hotelInfo, "lat", lat);
        setField(hotelInfo, "lon", lon);

        return hotelInfo;
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
        field.set(object, value);
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
        field.set(object, value);
    }
}