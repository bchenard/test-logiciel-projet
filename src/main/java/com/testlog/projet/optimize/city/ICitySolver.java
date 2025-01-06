package com.testlog.projet.optimize.city;

import com.testlog.projet.types.Activity;

import java.util.List;

public interface ICitySolver {
    /**
     * Solve the problem using linear programming.
     * The goal is to maximize the amount of activities between startDay and startDay + nbDays - 1
     *
     * @param activities list of activities
     * @param startDay   day of the week when the trip starts (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
     * @param nbDays     number of days of the trip (starting from the arrival day)
     * @param budget     budget for the whole trip in the city (not including transportation to and from the city)
     * @return List<Activity> where the list is indexed by days since startDay. If no activity is planned for a given day, the list will contain a null value.
     */
    List<Activity> solve(List<Activity> activities, int startDay, int nbDays, double budget);
}
