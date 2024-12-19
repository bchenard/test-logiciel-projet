package com.testlog.projet;

import com.testlog.projet.types.ActivityType;
import com.testlog.projet.types.Package;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PackageTest {

    @Test
    public void testGetActivities_emptyActivitiesList() {
        List<Activity> activities = new ArrayList<>();
        Hotel hotel = new Hotel("Test", 1.1, 1.1 , 3, "Test");  // assumes Hotel has a constructor without parameters
        com.testlog.projet.types.Package testPackage = new com.testlog.projet.types.Package(activities, hotel);

        List<Activity> retrievedActivities = testPackage.getActivities();
        Assertions.assertTrue(retrievedActivities.isEmpty());
    }

    @Test
    public void testGetActivities_singleActivityResult() {
        Activity activity = new Activity("Test_City", 1.1, 1.1, ActivityType.MUSIC, 100.0);
        List<Activity> activities = new ArrayList<>();
        activities.add(activity);

        Hotel hotel = new Hotel("Test", 1.1, 1.1 , 3, "Test");  // assumes Hotel has a constructor without parameters
        com.testlog.projet.types.Package testPackage = new com.testlog.projet.types.Package(activities, hotel);

        List<Activity> retrievedActivities = testPackage.getActivities();
        Assertions.assertEquals(1, retrievedActivities.size());
        Assertions.assertEquals(activity, retrievedActivities.get(0));
    }

    @Test
    public void testGetActivities_multipleActivityResults() {
        Activity activity1 = new Activity("Test_City1", 1.1, 1.1, ActivityType.CINEMA, 100.0);
        Activity activity2 = new Activity("Test_City2", 2.2, 2.2, ActivityType.MUSIC, 200.0);

        List<Activity> activities = new ArrayList<>();
        activities.add(activity1);
        activities.add(activity2);

        Hotel hotel = new Hotel("Test", 1.1, 1.1 , 3, "Test");  // assumes Hotel has a constructor without parameters
        com.testlog.projet.types.Package testPackage = new Package(activities, hotel);

        List<Activity> retrievedActivities = testPackage.getActivities();
        Assertions.assertEquals(2, retrievedActivities.size());
        Assertions.assertTrue(retrievedActivities.contains(activity1));
        Assertions.assertTrue(retrievedActivities.contains(activity2));
    }
}