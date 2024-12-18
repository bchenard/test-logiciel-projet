package com.testlog.projet.criteria;

import com.testlog.projet.types.TransportationMode;

public record TransportCriteria(TransportationMode preferredMode, boolean preferMinPricesOverMinDuration) {
}

