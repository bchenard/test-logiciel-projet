package com.testlog.projet.criteria;

import com.testlog.projet.types.TransportationMode;

public class TransportCriteria {
  private TransportationMode preferredMode;
  private boolean preferMinPricesOverMinDuration;

  public TransportCriteria(TransportationMode preferredMode, boolean preferMinPricesOverMinDuration) {
    this.preferredMode = preferredMode;
    this.preferMinPricesOverMinDuration = preferMinPricesOverMinDuration;
  }

  public TransportationMode getPreferredMode() {
    return preferredMode;
  }

  public boolean isPreferMinPricesOverMinDuration() {
    return preferMinPricesOverMinDuration;
  }
}

