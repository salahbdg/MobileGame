package com.bdg.mobilegame;


import java.util.List;

public class ChallengeManager {

    private static ChallengeManager instance;

    private List<Challenge> challenges;
    private int currentIndex = 0;
    private int score = 0;

    private ChallengeManager() {}

    public static ChallengeManager getInstance() {
        if (instance == null) {
            instance = new ChallengeManager();
        }
        return instance;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
        currentIndex = 0;
        score = 0;
    }

    public Challenge getCurrentChallenge() {
        if (challenges != null && currentIndex < challenges.size()) {
            return challenges.get(currentIndex);
        }
        return null;
    }

    public Class<?> getNextChallengeActivity() {
        currentIndex++;
        if (challenges != null && currentIndex < challenges.size()) {
            return challenges.get(currentIndex).getActivityToLaunch();
        }
        return null;
    }

    public boolean isLastChallenge() {
        return challenges == null || currentIndex >= challenges.size() - 1;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }
}
