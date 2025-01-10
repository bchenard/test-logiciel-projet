package com.testlog.projet.optimize;

import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.CityCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.types.Package;

public interface IOptimizer {
    Package solve(TransportCriteria transportCriteria, CityCriteria cityCriteria, AdditionalCriteria additionalCriteria);
}
