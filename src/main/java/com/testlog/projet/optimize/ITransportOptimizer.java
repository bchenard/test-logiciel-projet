package com.testlog.projet.optimize;

import com.testlog.projet.types.ComposedTrip;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.types.SimpleTrip;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransportOptimizer {

    List<SimpleTrip> filterTransportCriteria(List<SimpleTrip> Trips, TransportCriteria transportCriteria);
    ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime Date, TransportCriteria transportCriteria);
}
