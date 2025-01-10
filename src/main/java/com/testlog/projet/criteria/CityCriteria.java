package com.testlog.projet.criteria;

import com.testlog.projet.types.ActivityType;

import java.util.List;

public record CityCriteria(double maxActivityDistance, List<ActivityType> activityCategories,
                           boolean preferHotelWithMinPricesOverMaxStars, int hotelMinStars) {

}
