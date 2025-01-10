package com.testlog.projet.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlog.projet.services.io.IFileReader;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.LatLng;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityService implements ICityService<Activity> {

    private final Map<String, List<ActivityInfo>> cityData;
    private final IFileReader fileReader;

    public ActivityService(IFileReader fileReader) {
        this.fileReader = fileReader;
        this.cityData = loadCityData();
    }

    @Override
    public List<Activity> getForCity(String city, LocalDateTime date) {
        List<Activity> activities = new ArrayList<>();
        List<ActivityInfo> activityInfos = cityData.getOrDefault(city, List.of());

        for (ActivityInfo activityInfo : activityInfos) {
            activities.add(new Activity(
                    activityInfo.getName(),
                    activityInfo.getAddress(),
                    city,
                    new LatLng(Double.parseDouble(activityInfo.getLat()), Double.parseDouble(activityInfo.getLon())),
                    ActivityType.valueOf(activityInfo.getCategory().toUpperCase()),
                    activityInfo.getPrice(),
                    activityInfo.getDays()
            ));
        }
        return activities;
    }

    private Map<String, List<ActivityInfo>> loadCityData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = fileReader.readAll("src/main/resources/activities.json");
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load city data from activities.json", e);
        }
    }

    private static class ActivityInfo {
        @JsonProperty("category")
        private String category;

        @JsonProperty("price")
        private double price;

        @JsonProperty("name")
        private String name;

        @JsonProperty("address")
        private String address;

        @JsonProperty("lat")
        private String lat;

        @JsonProperty("lon")
        private String lon;

        @JsonProperty("days")
        private List<Boolean> days;

        public String getCategory() {
            return category;
        }

        public double getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        public List<Boolean> getDays() {
            return days;
        }
    }
}