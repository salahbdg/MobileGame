package com.bdg.mobilegame.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.R;

public class SwipeItActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private int score = 0;
    private final int requiredScore = 5;
    private TextView scoreText;
    private TextView timerText;
    private ImageView arrowImageView;

    private static final int SWIPE_UP = 0;
    private static final int SWIPE_DOWN = 1;
    private static final int SWIPE_LEFT = 2;
    private static final int SWIPE_RIGHT = 3;

    private int currentDirection = -1;
    private boolean arrowShowing = false;
    private Random random = new Random();
    private Handler handler = new Handler();

    // Arrow display duration and interval between arrows
    private static final int ARROW_DURATION_MS = 1000; // arrow interval
    private static final int ARROW_INTERVAL_MS = 800; // interval between arrow

    // Timer countdown
    private int timeRemaining = 10; // 10 seconds
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_it);

        Log.d("SwipeItActivity", "SwipeIt activity launched");

        scoreText = findViewById(R.id.swipeCounterText);
        timerText = findViewById(R.id.timerText);
        arrowImageView = findViewById(R.id.arrowImageView);

        // Initialize score display
        updateScoreDisplay();
        updateTimerDisplay();

        setupGestureDetector();

        // Start the game
        startGame();
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!arrowShowing) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                int detectedDirection;

                // Determine swipe direction
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Horizontal swipe
                    if (diffX > 0) {
                        detectedDirection = SWIPE_RIGHT;
                    } else {
                        detectedDirection = SWIPE_LEFT;
                    }
                } else {
                    // Vertical swipe
                    if (diffY > 0) {
                        detectedDirection = SWIPE_DOWN;
                    } else {
                        detectedDirection = SWIPE_UP;
                    }
                }

                // Check if swipe direction matches arrow direction
                if (detectedDirection == currentDirection) {
                    onCorrectSwipe();
                } else {
                    onWrongSwipe();
                }

                return true;
            }
        });
    }

    private void startGame() {
        // Start countdown timer
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeRemaining--;
                updateTimerDisplay();

                if (timeRemaining <= 0) {
                    endGame();
                } else {
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);

        // Start showing arrows
        showNextArrow();
    }

    private void showNextArrow() {
        if (timeRemaining <= 0) return;

        // Hide any previously shown arrow
        arrowImageView.setVisibility(View.INVISIBLE);
        arrowShowing = false;

        // Wait a bit before showing the next arrow
        handler.postDelayed(() -> {
            if (timeRemaining <= 0) return;

            // Generate random direction
            currentDirection = random.nextInt(4);

            // Set the correct arrow image
            switch (currentDirection) {
                case SWIPE_UP:
                    arrowImageView.setImageResource(R.drawable.arrow_up);
                    break;
                case SWIPE_DOWN:
                    arrowImageView.setImageResource(R.drawable.arrow_down);
                    break;
                case SWIPE_LEFT:
                    arrowImageView.setImageResource(R.drawable.arrow_left);
                    break;
                case SWIPE_RIGHT:
                    arrowImageView.setImageResource(R.drawable.arrow_right);
                    break;
            }

            // Show the arrow
            arrowImageView.setVisibility(View.VISIBLE);
            arrowShowing = true;

            // Hide the arrow after duration
            handler.postDelayed(() -> {
                arrowImageView.setVisibility(View.INVISIBLE);
                arrowShowing = false;

                // Schedule next arrow
                showNextArrow();
            }, ARROW_DURATION_MS);

        }, ARROW_INTERVAL_MS);
    }

    private void onCorrectSwipe() {
        score++;
        updateScoreDisplay();

        // Visual feedback
        Toast.makeText(this, "Good!", Toast.LENGTH_SHORT).show();

        // Hide the arrow immediately
        arrowImageView.setVisibility(View.INVISIBLE);
        arrowShowing = false;

        // Schedule next arrow
        handler.removeCallbacksAndMessages(null);
        showNextArrow();

        // Check if game is complete
        if (score >= requiredScore) {
            Toast.makeText(this, "Challenge Complete!", Toast.LENGTH_LONG).show();
            endGame();
        }
    }

    private void onWrongSwipe() {
        // Visual feedback
        Toast.makeText(this, "Wrong direction!", Toast.LENGTH_SHORT).show();
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + score + "/" + requiredScore);
    }

    private void updateTimerDisplay() {
        timerText.setText("Time: " + timeRemaining + "s");
    }

    private void endGame() {
        // Stop all handlers
        handler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacks(timerRunnable);

        // Hide arrow
        arrowImageView.setVisibility(View.INVISIBLE);

        if (score >= requiredScore) {
            // Challenge completed successfully
            // You can add ChallengeManager logic here
            Toast.makeText(this, "Challenge Complete!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Time's up! Score: " + score, Toast.LENGTH_LONG).show();
        }

        // Finish after a delay to show the toast
        handler.postDelayed(this::finish, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop all handlers when activity is paused
        handler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacks(timerRunnable);
    }
}