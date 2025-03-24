package com.bdg.mobilegame;

public class Challenge {
    private String description;
    private String type;  // e.g., "sensor", "motion", "question"
    private int points;

    public Challenge(String description, String type, int points) {
        this.description = description;
        this.type = type;
        this.points = points;
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
}
