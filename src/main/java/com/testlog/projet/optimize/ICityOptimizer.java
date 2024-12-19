package com.testlog.projet.optimize;

import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.Pair;

import java.util.List;

public interface ICityOptimizer {
    Pair<Hotel, List<Activity>> optimize(HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, String city, int days, int budget);
}
