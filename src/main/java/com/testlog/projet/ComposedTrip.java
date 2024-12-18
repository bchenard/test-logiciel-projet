package com.testlog.projet;

import java.util.ArrayList;
import java.util.List;

public class ComposedTrip {
  private List<SimpleTrip> trips;

  public ComposedTrip(List<SimpleTrip> trips) {
    this.trips = trips;
  }

  public String getDeparture_city() {
    return trips.get(0).getDeparture_city();
  }

  public String getArrival_city() {
    return trips.get(trips.size() - 1).getArrival_city();
  }

  public List<TransportationMode> getMode() {
    List<TransportationMode> modes = new ArrayList<>();
    for (SimpleTrip trip : trips) {
      modes.add(trip.getMode());
    }
    return modes;
  }

  public double getPrice() {
    double price = 0;
    for (SimpleTrip trip : trips) {
      price += trip.getPrice();
    }
    return price;
  }

  public long getDeparture_time() {
    return trips.get(0).getDeparture_time().getEpochSecond();
  }

  public long getArrival_time() {
    return trips.get(trips.size() - 1).getArrival_time().getEpochSecond();
  }
}
