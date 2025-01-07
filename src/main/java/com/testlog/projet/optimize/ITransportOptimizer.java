package com.testlog.projet.optimize;

import com.testlog.projet.ComposedTrip;

public interface ITransportOptimizer {
    ComposedTrip getOptimizedTrip(String origin, String destination);
}
