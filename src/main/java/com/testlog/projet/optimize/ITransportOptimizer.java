package com.testlog.projet.optimize;

import com.testlog.projet.ComposedTrip;

public interface ITransportOptimizer {

  public ComposedTrip getOptimizedTrip(String origin, String destination);

}
