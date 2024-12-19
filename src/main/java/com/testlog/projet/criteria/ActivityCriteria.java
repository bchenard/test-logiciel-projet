package com.testlog.projet.criteria;

import com.testlog.projet.types.Activity;
import com.testlog.projet.types.ActivityType;

import java.util.List;

public record ActivityCriteria (double maxDistance, List<ActivityType> categories){
}
