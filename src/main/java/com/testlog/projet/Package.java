package com.testlog.projet;

import java.util.List;

public class Package {

    private List<Activity> activities;
    private Hotel hotel;

    private ComposedTrip firstTrip;

    private ComposedTrip returnTrip;

    public ComposedTrip getFirstTrip() {
        return firstTrip;
    }

    public void setFirstTrip(ComposedTrip firstTrip) {
        this.firstTrip = firstTrip;
    }

    public ComposedTrip getReturnTrip() {
        return returnTrip;
    }

    public void setReturnTrip(ComposedTrip returnTrip) {
        this.returnTrip = returnTrip;
    }

    public Package(List<Activity> activities, Hotel hotel) {
        this.activities = activities;
        this.hotel = hotel;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities, ComposedTrip firstTrip, ComposedTrip returnTrip) {
        this.activities = activities;
        this.firstTrip = firstTrip;
        this.returnTrip = returnTrip;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }





}
