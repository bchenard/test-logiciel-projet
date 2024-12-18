package com.testlog.projet.criteria;

import java.time.Duration;
import java.time.LocalDate;

public class AdditionalCriteria {
  private LocalDate departureDate;
  private double maxDistance;
  private double maxPrice;
  private Duration duration;
  private String originCity;
  private String destinationCity;

  public AdditionalCriteria(LocalDate departureDate, double maxDistance, double maxPrice, Duration duration,
      String originCity, String destinationCity) {
    this.departureDate = departureDate;
    this.maxDistance = maxDistance;
    this.maxPrice = maxPrice;
    this.duration = duration;
    this.originCity = originCity;
    this.destinationCity = destinationCity;
  }

  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public double getMaxDistance() {
    return maxDistance;
  }

  public void setMaxDistance(double maxDistance) {
    this.maxDistance = maxDistance;
  }

  public double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(double maxPrice) {
    this.maxPrice = maxPrice;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  public String getOriginCity() {
    return originCity;
  }

  public void setOriginCity(String originCity) {
    this.originCity = originCity;
  }

  public String getDestinationCity() {
    return destinationCity;
  }

  public void setDestinationCity(String destinationCity) {
    this.destinationCity = destinationCity;
  }

}
