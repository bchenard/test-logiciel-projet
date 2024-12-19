package com.testlog.projet.services;

import com.testlog.projet.types.SimpleTrip;

import java.util.List;

public class TransportService implements ICityService<SimpleTrip> {
    @Override
    public List<SimpleTrip> getForCity(String city) {
        return List.of();
    }
}
