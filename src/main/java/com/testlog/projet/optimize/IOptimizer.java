package com.testlog.projet.optimize;

import com.testlog.projet.types.Package;
import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.criteria.TransportCriteria;

public interface IOptimizer {
    Package solve(TransportCriteria transportCriteria, HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, AdditionalCriteria additionalCriteria);
}
