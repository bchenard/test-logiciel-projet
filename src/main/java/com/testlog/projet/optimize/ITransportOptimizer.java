package com.testlog.projet.optimize;

import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.types.ComposedTrip;

import java.time.LocalDateTime;

public interface ITransportOptimizer {
    ComposedTrip getOptimizedTrip(String origin, String destination, LocalDateTime Date, TransportCriteria transportCriteria, Double maxPrice);
}
