package com.testlog.projet.services;

import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HotelServiceTest {

    private IFileReader fileReader;

    private final String hotelA = "{\"name\":\"Hotel A\",\"stars\":4,\"price\":150.0,\"address\":\"Address A\",\"lat\":\"48.8566\",\"lon\":\"2.3522\"}";
    private final String hotelB = "{\"name\":\"Hotel B\",\"stars\":5,\"price\":300.0,\"address\":\"Address B\",\"lat\":\"48.8584\",\"lon\":\"2.2945\"}";

    @BeforeEach
    public void setUp() {
        fileReader = mock(IFileReader.class);
    }

    @Test
    public void testGetForCity_IOError() throws IOException {
        when(fileReader.readAll(anyString())).thenThrow(new IOException());

        Exception exception = assertThrows(RuntimeException.class, () -> new HotelService(fileReader));
        assertTrue(exception.getMessage().contains("Failed to load city data from hotels.json"), "Wrong exception message");
    }

    @Test
    public void testGetForCity_withMultipleHotels_checkFields() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"Paris\":[" + hotelA + "," + hotelB + "]}");
        HotelService service = new HotelService(fileReader);

        List<Hotel> hotels = service.getForCity("Paris", any());

        assertEquals(2, hotels.size());

        Hotel firstHotel = hotels.getFirst();
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
    public void testGetForCity_nonExistentCity() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{}");
        HotelService service = new HotelService(fileReader);

        List<Hotel> hotels = service.getForCity("NonexistentCity", any());

        assertTrue(hotels.isEmpty());
    }

    @Test
    public void testGetForCity_withEmptyData() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{\"Paris\":[]}");
        HotelService service = new HotelService(fileReader);

        List<Hotel> hotels = service.getForCity("Paris", any());

        assertTrue(hotels.isEmpty());
    }

    @Test
    public void testGetForCity_invalidLatLon() throws IOException {
        String invalid = "{\"name\":\"Invalid Hotel\",\"stars\":3,\"price\":100.0,\"address\":\"Invalid Address\",\"lat\":\"invalid-lat\",\"lon\":\"invalid-lon\"}";
        when(fileReader.readAll(anyString())).thenReturn("{\"Paris\":[" + invalid + "]}");
        HotelService service = new HotelService(fileReader);

        assertThrows(NumberFormatException.class, () -> service.getForCity("Paris", any()));
    }

    @Test
    public void testGetForCity_multipleCities() throws IOException {
        String hotelC = "{\"name\":\"Hotel C\",\"stars\":3,\"price\":80.0,\"address\":\"Address C\",\"lat\":\"48.8606\",\"lon\":\"2.3376\"}";
        when(fileReader.readAll(anyString())).thenReturn("{\"Paris\":[" + hotelA + "," + hotelB + "],\"London\":[" + hotelC + "]}");
        HotelService service = new HotelService(fileReader);

        List<Hotel> parisHotels = service.getForCity("Paris", any());
        List<Hotel> londonHotels = service.getForCity("London", any());

        assertEquals(2, parisHotels.size(), "Expected two hotels in Paris");
        assertEquals(1, londonHotels.size(), "Expected one hotel in London");
        assertEquals("Hotel C", londonHotels.getFirst().name(), "Wrong hotel name in London");
    }

    @Test
    public void testLoadCityData_withEmptyMap() throws IOException {
        when(fileReader.readAll(anyString())).thenReturn("{}");
        HotelService service = new HotelService(fileReader);

        List<Hotel> hotels = service.getForCity("AnyCity", any());

        assertTrue(hotels.isEmpty(), "Expected no hotels when city data is empty");
    }

    @Test
    public void testGetForCity_withAddress() throws IOException {
        String hotelC = "{\"name\":\"Hotel A\",\"stars\":3,\"price\":100.0,\"address\":\"123 Main St\",\"lat\":\"48.8566\",\"lon\":\"2.3522\"}";
        when(fileReader.readAll(anyString())).thenReturn("{\"Paris\":[" + hotelC + "]}");
        HotelService hotelServiceSpy = new HotelService(fileReader);

        List<Hotel> hotels = hotelServiceSpy.getForCity("Paris", any());

        assertEquals(1, hotels.size());
        assertEquals("123 Main St", hotels.getFirst().address());
    }
}