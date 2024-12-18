package com.testlog.projet.criteria;

public class HotelCriteria {
  private boolean preferMinPricesOverMaxStars;
  private int minStars;

  public HotelCriteria(boolean preferMinPricesOverMaxStars, int minStars) {
    this.preferMinPricesOverMaxStars = preferMinPricesOverMaxStars;
    this.minStars = minStars;
  }
}
