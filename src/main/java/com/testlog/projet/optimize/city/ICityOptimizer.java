package com.testlog.projet.optimize.city;

import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.Pair;

import java.time.LocalDateTime;
import java.util.List;

public interface ICityOptimizer {
    /**
     * Returns the total price of a hotel over multiple days and a list of activities.
     */
    double getTotalPrice(Pair<Hotel, List<Activity>> pair, int nbDays);

    /**
     * Optimize a trip in a city.
     *
     * @param city             city where the trip will take place
     * @param startDay         day of the week when the trip starts (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
     * @param nbDays           number of days of the trip (starting from the arrival day)
     * @param budget           budget for the whole trip in the city (not including transportation to and from the city)
     * @param hotelCriteria    criteria for the hotel
     * @param activityCriteria criteria for the activities
     * @return Pair<Hotel, List < Activity>> where the list of activities is indexed by days since startDay.
     * If no activity is planned for a given day, the list will contain a null value.
     * If no hotel is found, it will return null.
     */
    Pair<Hotel, List<Activity>> optimize(String city, int startDay, int nbDays, double budget, HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, LocalDateTime date);
}
