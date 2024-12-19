package com.testlog.projet;


import com.testlog.projet.services.HotelService;
import com.testlog.projet.services.TransportService;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.SimpleTrip;

import java.util.List;
import java.util.Map;

public class Main {

        public static void main(String[] args) {
            try {
                HotelService hotelService = new HotelService();

                List<Hotel> rennesHotels = hotelService.getForCity("Bordeaux");
                rennesHotels.forEach(System.out::println);

                List<Hotel> unknownCityHotels = hotelService.getForCity("UnknownCity");
                System.out.println("Hotels in Unknown City: " + unknownCityHotels);

            } catch (Exception e) {
                // Handle any exceptions
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
