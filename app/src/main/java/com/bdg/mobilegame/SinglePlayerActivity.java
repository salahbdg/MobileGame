package com.bdg.mobilegame;

import android.content.Intent;
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


        // Initialize challenges and start
        initializeChallengesAndStart(); // Start the challenge chain!

    }

    private void initializeChallengesAndStart() {
        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(new Challenge("Shake It!", "sensor", 10, com.bdg.mobilegame.challenges.ShakeIt.class));
        challengeList.add(new Challenge("Catch the Dot", "motion", 10, com.bdg.mobilegame.challenges.CatchDotActivity.class));
        challengeList.add(new Challenge("Swipe It!", "motion", 10, com.bdg.mobilegame.challenges.SwipeItActivity.class));
        // Add 3 more as you build them

        ChallengeManager.getInstance().setChallenges(challengeList);

        Class<?> firstActivity = ChallengeManager.getInstance().getCurrentChallenge().getActivityToLaunch();
        if (firstActivity != null) {
            Intent intent = new Intent(this, firstActivity);
            startActivity(intent);
            finish();
        }
    }


    // not used

    private void initializeChallenges() {
        challengeList = new ArrayList<>();
        challengeList.add(new Challenge("Catch the dot", "motion", 10, com.bdg.mobilegame.challenges.CatchDotActivity.class));
        challengeList.add(new Challenge("Swipe the screen 3 times", "motion", 5, com.bdg.mobilegame.challenges.SwipeItActivity.class));
        challengeList.add(new Challenge("What is 5 + 3?", "question", 15, null));
        challengeList.add(new Challenge("Shake your phone vigorously", "sensor", 10, com.bdg.mobilegame.challenges.ShakeIt.class));
        challengeList.add(new Challenge("Swipe up 5 times", "motion", 5, null));
        challengeList.add(new Challenge("What is the capital of France?", "question", 15, null));
    }


    private void displayNextChallenge() {
        if (currentChallengeIndex < challengeList.size()) {
            Challenge currentChallenge = challengeList.get(currentChallengeIndex);
            challengeText.setText("Challenge: " + currentChallenge.getDescription());

            // Launch the actual game if there's a challenge activity
            if (currentChallenge.getActivityToLaunch() != null) {
                Intent intent = new Intent(SinglePlayerActivity.this, currentChallenge.getActivityToLaunch());
                startActivity(intent);
            }

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
