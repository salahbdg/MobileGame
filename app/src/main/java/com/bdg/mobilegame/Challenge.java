package com.bdg.mobilegame;

public class Challenge {
    private String description;
    private String type;
    private int points;
    private Class<?> activityToLaunch;

    public Challenge(String description, String type, int points, Class<?> activityToLaunch) {
        this.description = description;
        this.type = type;
        this.points = points;
        this.activityToLaunch = activityToLaunch;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public Class<?> getActivityToLaunch() {
        return activityToLaunch;
    }
}
