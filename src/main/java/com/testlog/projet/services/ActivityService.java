package com.testlog.projet.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.ActivityType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityService implements ICityService<Activity> {

    private final Map<String, List<ActivityInfo>> cityData;

    public ActivityService() {
        this.cityData = loadCityData();
    }

    @Override
    public List<Activity> getForCity(String city) {
        List<Activity> activities = new ArrayList<>();
        List<ActivityInfo> activityInfos = cityData.getOrDefault(city, List.of());

        for (ActivityInfo activityInfo : activityInfos) {
            activities.add(new Activity(
                    city,
                    Double.parseDouble(activityInfo.getLat()),
                    Double.parseDouble(activityInfo.getLon()),
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
            String json = Files.readString(Paths.get("src/main/resources/activites.json"));
            return mapper.readValue(json, new TypeReference<>() {});
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