package com.testlog.projet.services;

import com.testlog.projet.types.Hotel;

import java.util.List;

public class HotelService implements ICityService<Hotel> {
    @Override
    public List<Hotel> getForCity(String city) {
        return List.of();
    }
}
