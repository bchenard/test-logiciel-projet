package com.testlog.projet.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.LatLng;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HotelService implements ICityService<Hotel> {

    private final Map<String, List<HotelInfo>> cityData;

    public HotelService() {
        this.cityData = loadCityData();
    }

    @Override
    public List<Hotel> getForCity(String city) {
        List<Hotel> hotels = new ArrayList<>();
        List<HotelInfo> hotelInfos = cityData.getOrDefault(city, List.of());

        for (HotelInfo hotelInfo : hotelInfos) {
            hotels.add(new Hotel(
                    city,
                    new LatLng(Double.parseDouble(hotelInfo.getLat()), Double.parseDouble(hotelInfo.getLon())),
                    hotelInfo.getStars(),
                    hotelInfo.getName(),
                    hotelInfo.getPrice(),
                    hotelInfo.getAddress()
            ));
        }
        return hotels;
    }

    private Map<String, List<HotelInfo>> loadCityData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = Files.readString(Paths.get("src/main/resources/hotels.json"));
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load city data from hotels.json", e);
        }
    }

    // Nested class for JSON deserialization
    private static class HotelInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("stars")
        private int stars;

        @JsonProperty("price")
        private double price;

        @JsonProperty("address")
        private String address;

        @JsonProperty("lat")
        private String lat;

        @JsonProperty("lon")
        private String lon;

        public String getName() { return name; }
        public int getStars() { return stars; }
        public double getPrice() { return price; }
        public String getAddress() { return address; }
        public String getLat() { return lat; }
        public String getLon() { return lon; }
    }
}