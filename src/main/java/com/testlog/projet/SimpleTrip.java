package com.testlog.projet;

import java.time.Instant;

public class SimpleTrip {
  private String departureCity;
  private String arrivalCity;
  private TransportationMode mode;
  private double price;
  private Instant departureTime;
  private Instant arrivalTime;

  public SimpleTrip(String departureCity, String arrivalCity, TransportationMode mode, double price,
                    Instant departureTime, Instant arrivalTime) {
    this.departureCity = departureCity;
    this.arrivalCity = arrivalCity;
    this.mode = mode;
    this.price = price;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
  }

  public String getDepartureCity() {
    return departureCity;
  }

  public void setDepartureCity(String departureCity) {
    this.departureCity = departureCity;
  }

  public String getArrivalCity() {
    return arrivalCity;
  }

  public void setArrivalCity(String arrivalCity) {
    this.arrivalCity = arrivalCity;
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

  public Instant getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(Instant departureTime) {
    this.departureTime = departureTime;
  }

  public Instant getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(Instant arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

}
