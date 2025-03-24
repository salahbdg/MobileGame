package com.bdg.mobilegame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SinglePlayerActivity extends AppCompatActivity {

    private int score = 0;
    private List<Challenge> challengeList;
    private int currentChallengeIndex = 0;
    private TextView challengeText;
    private TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        challengeText = findViewById(R.id.challengeText);
        scoreText = findViewById(R.id.scoreText);
        Button nextChallengeButton = findViewById(R.id.nextChallengeButton);

        // Initialize challenges
        initializeChallenges();

        // Display the first challenge
        displayNextChallenge();

        // Set up button click listener for the next challenge
        nextChallengeButton.setOnClickListener(v -> {
            // Simulate scoring for the current challenge
            score += getRandomScore();
            scoreText.setText("Score: " + score);
            displayNextChallenge();
        });
    }

    private void initializeChallenges() {
        // Populate the challenge list (you can add more challenges)
        challengeList = new ArrayList<>();
        challengeList.add(new Challenge("Tilt your phone to the left", "sensor", 10));
        challengeList.add(new Challenge("Swipe the screen 3 times", "motion", 5));
        challengeList.add(new Challenge("What is 5 + 3?", "question", 15));
        challengeList.add(new Challenge("Shake your phone vigorously", "sensor", 10));
        challengeList.add(new Challenge("Swipe up 5 times", "motion", 5));
        challengeList.add(new Challenge("What is the capital of France?", "question", 15));
    }

    private void displayNextChallenge() {
        if (currentChallengeIndex < challengeList.size()) {
            Challenge currentChallenge = challengeList.get(currentChallengeIndex);
            challengeText.setText("Challenge: " + currentChallenge.getDescription());
            currentChallengeIndex++;
        } else {
            challengeText.setText("Game Over! Your score: " + score);
        }
    }

    private int getRandomScore() {
        // Simulate a random score between 1 and 10
        Random random = new Random();
        return random.nextInt(10) + 1;
    }
}
