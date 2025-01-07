package com.testlog.projet.optimize;

import com.testlog.projet.ComposedTrip;
import com.testlog.projet.criteria.ActivityCriteria;
import com.testlog.projet.criteria.AdditionalCriteria;
import com.testlog.projet.criteria.HotelCriteria;
import com.testlog.projet.criteria.TransportCriteria;
import com.testlog.projet.optimize.city.ICityOptimizer;
import com.testlog.projet.types.Activity;
import com.testlog.projet.types.Hotel;
import com.testlog.projet.types.Package;
import com.testlog.projet.types.Pair;

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
    public Package solve(TransportCriteria transportCriteria, HotelCriteria hotelCriteria, ActivityCriteria activityCriteria, AdditionalCriteria other) {
        String origin = other.originCity();
        String destination = other.destinationCity();

        ComposedTrip forward = transportOptimizer.getOptimizedTrip(origin, destination);
        ComposedTrip backward = transportOptimizer.getOptimizedTrip(destination, origin);

        LocalDateTime arrival = forward.getArrivalTime();
        LocalDateTime departure = backward.getDepartureTime();

        int startDay = arrival.getDayOfWeek().getValue() - 1;
        int nbDays = (int) arrival.until(departure, ChronoUnit.DAYS);

        double transportCost = forward.getPrice() + backward.getPrice();
        double newBudget = other.maxPrice() - transportCost;
        Pair<Hotel, List<Activity>> cityTrip;
        cityTrip = cityOptimizer.optimize(destination,startDay, nbDays, newBudget, hotelCriteria, activityCriteria);

        double totalPrice = transportCost + cityOptimizer.getTotalPrice(cityTrip);

        return new Package(cityTrip.second(), cityTrip.first(), forward, backward, totalPrice);
    }
}
