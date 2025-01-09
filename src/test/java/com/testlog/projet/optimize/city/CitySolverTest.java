package com.testlog.projet.optimize.city;

import com.testlog.projet.types.Activity;
import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.LatLng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CitySolverTest {
    CitySolver solver;

    @BeforeEach
    public void setUp() {
        solver = new CitySolver();
    }

    final Activity activityA = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(true, true, true, true, true, true, true));
    final Activity activityB = new Activity("name", "address", "Bordeaux", new LatLng(1.3, 1.3), ActivityType.CINEMA, 75., List.of(true, true, true, true, true, true, true));
    final Activity activityC = new Activity("name", "address", "Bordeaux", new LatLng(1.5, 1.5), ActivityType.SPORT, 100., List.of(true, true, true, true, true, true, true));

    @Test
    public void testSolve_withNoDays() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 0, 1000);

        assertEquals(0, result.size());
    }

    @Test
    public void testSolve_withOneDay() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 1, 1000);

        assertEquals(1, result.size());
    }

    @Test
    public void testSolve_withMultipleDays() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        assertNotEquals(result.get(0), result.get(1));
        assertNotNull(result.get(0));
        assertNotNull(result.get(1));
    }

    @Test
    public void testSolve_withNoBudget() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 2, 0);

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    public void testSolve_withLargeBudget() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 2, 10000);

        assertEquals(2, result.size());
        assertNotEquals(result.get(0), result.get(1));
        assertNotNull(result.get(0));
        assertNotNull(result.get(1));
    }

    @Test
    public void testSolve_withoutActivities() {
        List<Activity> activities = List.of();

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    public void testSolve_withOneActivity() {
        List<Activity> activities = List.of(activityA);

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        // Either the first activity is null or the second one is null
        assertTrue(result.get(0) == null || result.get(1) == null);
    }

    @Test
    public void testSolve_withMultipleActivities_andOneDay() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 1, 1000);

        assertEquals(1, result.size());
        assertNotNull(result.getFirst());
    }

    @Test
    public void testSolve_withNoAvailability() {
        Activity a1 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(false, false, false, false, false, false, false));
        Activity a2 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, false, false, false, false, false));
        List<Activity> activities = List.of(a1, a2);

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    public void testSolve_withSomeAvailability_noDayPossible() {
        Activity a1 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(false, false, true, false, false, false, false));
        Activity a2 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, true, true, true, false, false));
        List<Activity> activities = List.of(a1, a2);

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    public void testSolve_withSomeAvailability_allDaysPossible() {
        Activity a1 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(true, true, true, false, false, false, false));
        Activity a2 = new Activity("name", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, true, true, true, true, false, false));
        List<Activity> activities = List.of(a1, a2);

        List<Activity> result = solver.solve(activities, 0, 2, 1000);

        assertEquals(2, result.size());
        assertEquals(a1, result.get(0));
        assertEquals(a2, result.get(1));
    }

    @Test
    public void testSolve_withFullWeek() {
        List<Activity> activities = List.of(
                new Activity("1", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CULTURE, 50., List.of(false, false, true, false, false, false, true)),
                new Activity("2", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, false, false, false, true, false)),
                new Activity("3", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, false, false, true, false, false)),
                new Activity("4", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, false, true, false, false, false)),
                new Activity("5", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, false, true, false, false, false, false)),
                new Activity("6", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(false, true, false, false, false, false, false)),
                new Activity("7", "address", "Bordeaux", new LatLng(1., 1.), ActivityType.CINEMA, 20., List.of(true, false, false, false, false, false, false))
        );

        List<Activity> result = solver.solve(activities, 0, 7, 10000);

        assertEquals(7, result.size());
        assertEquals(activities.get(6), result.get(0));
        assertEquals(activities.get(5), result.get(1));
        assertEquals(activities.get(4), result.get(2));
        assertEquals(activities.get(3), result.get(3));
        assertEquals(activities.get(2), result.get(4));
        assertEquals(activities.get(1), result.get(5));
        assertEquals(activities.get(0), result.get(6));
    }

    @Test
    public void testSolve_withBudgetForOneActivity() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 2, 50);

        assertEquals(2, result.size());
        assertTrue(result.contains(activityA));
        assertFalse(result.contains(activityB));
    }

    @Test
    public void testSolve_withNegativeBudget() {
        List<Activity> activities = List.of(activityA, activityB, activityC);

        List<Activity> result = solver.solve(activities, 0, 2, -100);

        assertEquals(2, result.size());
        assertNull(result.get(0));
        assertNull(result.get(1));
    }
}
