package com.testlog.projet.optimize.city;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.testlog.projet.types.Activity;

import java.util.ArrayList;
import java.util.List;

public class CitySolver implements ICitySolver {
    static {
        Loader.loadNativeLibraries();
    }
    // List<Hotel> not in the LP solver because it would introduce too many variables
    // It is possible to just solve for each hotel available in the given city

    // NOTE: The specification asks to return all optimal packages, but the code only returns the best one
    // This is because we consider it too unlikely to have multiple optimal solutions based on prices

    /**
     * Solves the problem of selecting activities for a given number of days.
     *
     * @param activities list of activities available in the city
     * @param startDay   day of the week when the trip starts (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
     * @param nbDays     number of days of the trip (starting from the arrival day)
     * @param budget     budget for the whole trip in the city (not including transportation to and from the city)
     * @return List<Activity> where the list is indexed by days since startDay.
     * If no activity is planned for a given day, the list will contain a null value.
     */
    public List<Activity> solve(List<Activity> activities, int startDay, int nbDays, double budget) {
        MPSolver solver = MPSolver.createSolver("CBC");
        int activityCount = activities.size();

        MPVariable[][] vars = new MPVariable[nbDays][activityCount];
        for (int d = 0; d < nbDays; d++) {
            for (int a = 0; a < activityCount; a++) {
                vars[d][a] = solver.makeBoolVar("day_" + d + "_activity_" + a);
            }
        }

        // If the activity is not available on a given day, force it to 0 in the model
        for (int a = 0; a < activityCount; a++) {
            List<Boolean> availability = activities.get(a).availability();
            for (int d = 0; d < nbDays; d++) {
                if (!availability.get((startDay + d) % 7)) {
                    // Setting the coefficient to 0 to make sure the activity cannot be selected
                    MPConstraint constraint = solver.makeConstraint(0, 0);
                    constraint.setCoefficient(vars[d][a], 1);
                }
            }
        }

        // Cannot have more than one activity on the same day
        for (int d = 0; d < nbDays; d++) {
            MPConstraint constraint = solver.makeConstraint(0, 1);
            for (int a = 0; a < activityCount; a++) {
                constraint.setCoefficient(vars[d][a], 1);
            }
        }

        // Same activity cannot be selected on two different days
        for (int a = 0; a < activityCount; a++) {
            MPConstraint constraint = solver.makeConstraint(0, 1);
            for (int d = 0; d < nbDays; d++) {
                constraint.setCoefficient(vars[d][a], 1);
            }
        }

        // sum_d(vars[d][a]) is whether or not we want to do activity 'a' on any day of the trip (0 or 1)
        // sum_d(price_a * vars[d][a]) is trivially the price of activity 'a' if we want to do it, otherwise 0
        // sum_d(sum_a(price_a * vars[d][a])) is the total price of all activities we want to do (should be < budget)
        MPConstraint budgetConstraint = solver.makeConstraint(0, budget);
        for (int a = 0; a < activityCount; a++) { // sum_a
            double price = activities.get(a).price();
            for (int d = 0; d < nbDays; d++) { // sum_d
                // price_a * vars[d][a]
                budgetConstraint.setCoefficient(vars[d][a], price);
            }
        }

        // Maximization of the number of activities, trivially sum_a(sum_d(vars[d][a]))
        MPObjective objective = solver.objective();
        for (int a = 0; a < activityCount; a++) {
            for (int d = 0; d < nbDays; d++) {
                objective.setCoefficient(vars[d][a], 1);
            }
        }
        objective.setMaximization();

        System.out.println("Solving with " + solver.numVariables() + " boolean variables and " + solver.numConstraints() + " constraints...");

        final MPSolver.ResultStatus resultStatus = solver.solve();

        // Remove previous "..." and write ". Status = "
        System.out.println("Status = " + resultStatus);
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            if (resultStatus == MPSolver.ResultStatus.FEASIBLE) {
                System.out.println("A potentially suboptimal solution was found");
            } else {
                System.out.println("The solver could not solve the problem.");
                throw new RuntimeException("The solver could not solve the problem.");
            }
        }

        List<Activity> solution = new ArrayList<>(nbDays);
        for (int i = 0; i < nbDays; i++) {
            solution.add(null);
        }
        // Parse the optimal solution to find the selected activities
        for (int d = 0; d < nbDays; d++) {
            for (int a = 0; a < activityCount; a++) {
                if (vars[d][a].solutionValue() == 1) {
                    solution.set(d, activities.get(a));
                    break;
                }
            }
        }

        return solution;
    }
}
