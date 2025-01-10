package com.testlog.projet.optimize;

import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.CityCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.city.ICityOptimizer;
import com.testlog.projet.types.Package;
import com.testlog.projet.types.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Optimizer implements IOptimizer {

    private final ITransportOptimizer transportOptimizer;
    private final ICityOptimizer cityOptimizer;

    public Optimizer(ITransportOptimizer transportOptimizer, ICityOptimizer cityOptimizer) {
        this.transportOptimizer = transportOptimizer;
        this.cityOptimizer = cityOptimizer;
    }

    @Override
    public Package solve(TransportCriteria transportCriteria, CityCriteria cityCriteria, AdditionalCriteria other) {
        String origin = other.originCity();
        String destination = other.destinationCity();

        LocalDateTime returnDate = other.departureDate().plus(other.duration());
        ComposedTrip forward = transportOptimizer.getOptimizedTrip(origin, destination, other.departureDate(), transportCriteria, other.maxPrice());
        ComposedTrip backward = transportOptimizer.getOptimizedTrip(destination, origin, returnDate, transportCriteria, other.maxPrice() - forward.getPrice());

        LocalDateTime arrival = forward.getArrivalTime();
        LocalDateTime departure = backward.getDepartureTime();

        int startDay = arrival.getDayOfWeek().getValue() - 1;
        int nbDays = (int) arrival.until(departure, ChronoUnit.DAYS);


        double transportCost = forward.getPrice() + backward.getPrice();
        double newBudget = other.maxPrice() - transportCost;
        if (newBudget < 0) {
            return new Package(null, null, forward, backward, transportCost);
        }

        Pair<Hotel, List<Activity>> cityTrip;
        cityTrip = cityOptimizer.optimize(destination, startDay, nbDays, newBudget, cityCriteria, arrival);

        double totalPrice = transportCost + cityOptimizer.getTotalPrice(cityTrip, nbDays);

        return new Package(cityTrip.second(), cityTrip.first(), forward, backward, totalPrice);
    }
}
