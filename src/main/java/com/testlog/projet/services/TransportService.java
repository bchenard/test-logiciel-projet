package com.testlog.projet.services;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.SimpleTrip;
import com.testlog.projet.types.TransportationMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportService implements ICityService<SimpleTrip> {

    private final Map<String, List<ConnectionInfo>> cityData;
    private final IFileReader fileReader;

    public TransportService(IFileReader fileReader) {
        this.fileReader = fileReader;
        this.cityData = loadCityData();
    }

    @Override
    public List<SimpleTrip> getForCity(String city) {
        // The original overridden method (for today's date)
        List<SimpleTrip> trips = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<ConnectionInfo> connections = cityData.getOrDefault(city, List.of());

        for (ConnectionInfo connection : connections) {
            for (Schedule schedule : connection.getHours()) {
                LocalDateTime departureTime = parseTime(today, schedule.getStart());
                LocalDateTime arrivalTime = parseTime(today, schedule.getEnd());
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

    /**
     * New method that allows specifying a date using LocalDateTime.
     */
    public List<SimpleTrip> getForCity(String city, LocalDateTime dateTime) {
        // 1. Get trips for "today" using the overridden method
        List<SimpleTrip> trips = getForCity(city);

        // 2. Calculate the offset between today's start and the specified date
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime targetStart = dateTime.toLocalDate().atStartOfDay();
        long dayOffset = ChronoUnit.DAYS.between(todayStart, targetStart);
        long timeOffsetSeconds = ChronoUnit.SECONDS.between(todayStart, dateTime);

        // 3. Create a new list with shifted times
        List<SimpleTrip> adjustedTrips = new ArrayList<>();
        for (SimpleTrip trip : trips) {
            LocalDateTime shiftedDeparture = trip.departureTime().plusDays(dayOffset).plusSeconds(timeOffsetSeconds % (24 * 60 * 60));
            LocalDateTime shiftedArrival = trip.arrivalTime().plusDays(dayOffset).plusSeconds(timeOffsetSeconds % (24 * 60 * 60));

            adjustedTrips.add(new SimpleTrip(
                    trip.departureCity(),
                    trip.arrivalCity(),
                    trip.mode(),
                    trip.price(),
                    shiftedDeparture,
                    shiftedArrival
            ));
        }

        return adjustedTrips;
    }

    private Map<String, List<ConnectionInfo>> loadCityData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = fileReader.readAll("src/main/resources/trips.json");
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load city data from trips.json", e);
        }
    }

    private LocalDateTime parseTime(LocalDate date, String time) {
        return LocalDateTime.of(
                date.getYear(), date.getMonth(), date.getDayOfMonth(),
                Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1])
        );
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