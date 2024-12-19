package com.testlog.projet.services;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportService implements ICityService<SimpleTrip> {

    private final Map<String, List<ConnectionInfo>> cityData;

    public TransportService() {
        this.cityData = loadCityData();
    }

    @Override
    public List<SimpleTrip> getForCity(String city) {
        List<SimpleTrip> trips = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<ConnectionInfo> connections = cityData.getOrDefault(city, List.of());

        for (ConnectionInfo connection : connections) {
            for (Schedule schedule : connection.getHours()) {
                Instant departureTime = parseTime(today, schedule.getStart());
                Instant arrivalTime = parseTime(today, schedule.getEnd());
                trips.add(new SimpleTrip(
                        city,
                        connection.getDestination(),
                        TransportationMode.valueOf(connection.getMode().toUpperCase()),
                        connection.getPrice(),
                        departureTime,
                        arrivalTime
                ));
            }
        }
        return trips;
    }

    private Map<String, List<ConnectionInfo>> loadCityData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = Files.readString(Paths.get("src/main/resources/trips.json"));
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load city data from trips.json", e);
        }
    }

    private Instant parseTime(LocalDate date, String time) {
        LocalDateTime dateTime = LocalDateTime.of(
                date.getYear(), date.getMonth(), date.getDayOfMonth(),
                Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1])
        );
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    // Nested classes for JSON deserialization
    private static class Schedule {
        @JsonProperty("start")
        private String start;

        @JsonProperty("end")
        private String end;

        public String getStart() { return start; }
        public String getEnd() { return end; }
    }

    private static class ConnectionInfo {
        @JsonProperty("destination")
        private String destination;

        @JsonProperty("mode")
        private String mode;

        @JsonProperty("price")
        private double price;

        @JsonProperty("hours")
        private List<Schedule> hours;

        public String getDestination() { return destination; }
        public String getMode() { return mode; }
        public double getPrice() { return price; }
        public List<Schedule> getHours() { return hours; }
    }
}