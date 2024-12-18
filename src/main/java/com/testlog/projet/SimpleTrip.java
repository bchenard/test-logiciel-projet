package com.testlog.projet;

import java.time.Instant;

public class SimpleTrip {
  private String departure_city;
  private String arrival_city;
  private TransportationMode mode;
  private double price;
  private Instant departure_time;
  private Instant arrival_time;

  public SimpleTrip(String departure_city, String arrival_city, TransportationMode mode, double price,
      Instant departure_time, Instant arrival_time) {
    this.departure_city = departure_city;
    this.arrival_city = arrival_city;
    this.mode = mode;
    this.price = price;
    this.departure_time = departure_time;
    this.arrival_time = arrival_time;
  }

  public String getDeparture_city() {
    return departure_city;
  }

  public void setDeparture_city(String departure_city) {
    this.departure_city = departure_city;
  }

  public String getArrival_city() {
    return arrival_city;
  }

  public void setArrival_city(String arrival_city) {
    this.arrival_city = arrival_city;
  }

  public TransportationMode getMode() {
    return mode;
  }

  public void setMode(TransportationMode mode) {
    this.mode = mode;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public Instant getDeparture_time() {
    return departure_time;
  }

  public void setDeparture_time(Instant departure_time) {
    this.departure_time = departure_time;
  }

  public Instant getArrival_time() {
    return arrival_time;
  }

  public void setArrival_time(Instant arrival_time) {
    this.arrival_time = arrival_time;
  }

}
