package com.testlog.projet.services;

import com.testlog.projet.types.Activity;

import java.util.List;

public class ActivityService implements ICityService<Activity> {
    @Override
    public List<Activity> getForCity(String city) {
        return List.of();
    }
}
