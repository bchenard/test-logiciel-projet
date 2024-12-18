package com.testlog.projet;

import java.util.ArrayList;
import java.util.List;

public class ComposedTrip {
  private final List<SimpleTrip> trips;

  public ComposedTrip(List<SimpleTrip> trips) {
    this.trips = trips;
  }

  public String getDepartureCity() {
    return trips.getFirst().getDepartureCity();
  }

  public String getArrivalCity() {
    return trips.getLast().getArrivalCity();
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

  public long getDepartureTime() {
    return trips.getFirst().getDepartureTime().getEpochSecond();
  }

  public long getArrivalTime() {
    return trips.getLast().getArrivalTime().getEpochSecond();
  }
}
