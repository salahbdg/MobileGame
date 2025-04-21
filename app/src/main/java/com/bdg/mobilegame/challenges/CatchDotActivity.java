package com.bdg.mobilegame.challenges;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.GameOver;
import com.bdg.mobilegame.R;

import java.util.ArrayList;
import java.util.Random;

public class CatchDotActivity extends AppCompatActivity {

    private FrameLayout gameArea;
    private ArrayList<ImageView> dots = new ArrayList<>();
    private TextView scoreText;
    private TextView levelText;
    private TextView timerText;
    private TextView multiplierText;

    private int score = 0;
    private int level = 1;
    private int dotsToSpawn = 1;
    private int scoreMultiplier = 1;
    private int streakCount = 0;
    private long lastDotTapTime = 0;

    private Handler handler = new Handler();
    private Random random = new Random();

    private static final int MAX_LEVEL = 20;
    private static final int DOTS_PER_LEVEL = 10;
    private static final long TIME_LIMIT_BASE = 5000; // 20 seconds base time
    private static final int DOT_COLORS[] = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA
    };

    private CountDownTimer timer;
    private long timeRemaining;
    private boolean gameActive = true;

    // Bonus settings
    private ImageView bonusDot;
    private static final int BONUS_CHANCE = 15; // % chance for bonus to appear
    private static final int BONUS_POINTS = 5;
    private static final int STREAK_THRESHOLD = 3; // Taps needed for streak multiplier

    // Movement animation
    private boolean dotsMoving = false;
    private ArrayList<Animator> dotAnimations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_dot);

        Log.d("CatchDotActivity", "Activity launched");

        // Initialize UI elements
        gameArea = findViewById(R.id.gameArea);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        levelText = findViewById(R.id.levelText);
        multiplierText = findViewById(R.id.multiplierText);

        // Initialize bonus dot
        bonusDot = findViewById(R.id.bonusDot);
        if (bonusDot != null) {
            bonusDot.setVisibility(View.GONE);
            bonusDot.setOnClickListener(v -> onBonusDotClick());
        }

        updateScoreText();
        updateLevelText();
        updateMultiplierText();

        // Start game when layout is ready
        gameArea.post(this::startLevel);
    }

    private void startLevel() {
        // Clear existing dots
        for (ImageView dot : dots) {
            gameArea.removeView(dot);
        }
        dots.clear();

        // Cancel any existing animations
        for (Animator anim : dotAnimations) {
            anim.cancel();
        }
        dotAnimations.clear();

        // Update level display
        updateLevelText();

        // Calculate dots to spawn based on level
        dotsToSpawn = Math.min(level + 1, 5);

        // Create new dots
        for (int i = 0; i < dotsToSpawn; i++) {
            spawnDot();
        }

        // Maybe spawn a bonus dot
        if (random.nextInt(100) < BONUS_CHANCE) {
            spawnBonusDot();
        }

        // Start timer for the level
        long timeForLevel = TIME_LIMIT_BASE - ((level - 1) * 2000); // Decrease time as level increases
        timeForLevel = Math.max(timeForLevel, 5000); // Minimum 5 seconds

        startTimer(timeForLevel);

        // Start dot movement animation if level > 1
        if (level > 1) {
            startDotMovement();
        }
    }

    private void spawnDot() {
        ImageView dot = new ImageView(this);
        dot.setImageResource(R.drawable.dot);

        // Random size based on level (smaller as level increases)
        int baseDotSize = 120 - ((level - 1) * 15);
        baseDotSize = Math.max(baseDotSize, 50); // Minimum size

        // Set dot size
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(baseDotSize, baseDotSize);
        dot.setLayoutParams(params);

        // Set random color
        dot.setColorFilter(DOT_COLORS[random.nextInt(DOT_COLORS.length)]);

        // Add to game area
        gameArea.addView(dot);
        dots.add(dot);

        // Position randomly
        positionDotRandomly(dot);

        // Add click listener
        dot.setOnClickListener(v -> onDotClick(dot));
    }

    private void spawnBonusDot() {
        if (bonusDot == null) return;

        // Make bonus dot visible
        bonusDot.setVisibility(View.VISIBLE);

        // Smaller and golden
        int bonusDotSize = 60;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bonusDotSize, bonusDotSize);
        bonusDot.setLayoutParams(params);
        bonusDot.setColorFilter(Color.rgb(255, 215, 0)); // Gold color

        // Position randomly
        positionDotRandomly(bonusDot);

        // Add disappear animation
        handler.postDelayed(() -> {
            if (bonusDot.getVisibility() == View.VISIBLE) {
                fadeOutView(bonusDot);
            }
        }, 3000); // Disappears after 3 seconds
    }

    private void onDotClick(ImageView dot) {
        if (!gameActive) return;

        // Add score based on multiplier
        score += scoreMultiplier;
        updateScoreText();

        // Track streak (consecutive taps within 1 second)
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDotTapTime < 1000) {
            streakCount++;
            if (streakCount >= STREAK_THRESHOLD) {
                scoreMultiplier = Math.min(scoreMultiplier + 1, 5);
                updateMultiplierText();
                showToast("Multiplier x" + scoreMultiplier + "!");
            }
        } else {
            streakCount = 1;
        }
        lastDotTapTime = currentTime;

        // Show tap animation
        showTapAnimation(dot);

        // Remove dot
        gameArea.removeView(dot);
        dots.remove(dot);

        // Check if we need to spawn new dots or advance level
        if (dots.isEmpty()) {
            // Player caught all dots
            if (level < MAX_LEVEL) {
                level++;
                // Give bonus time
                if (timer != null) {
                    timer.cancel();
                }
                timeRemaining += 5000; // 5 second bonus for clearing level
                showToast("Level up! +5 seconds");
                startLevel();
            } else {
                // Game completed successfully!
                completeChallenge(true);
            }
        } else if (score >= level * DOTS_PER_LEVEL) {
            // Advance to next level based on score
            level++;
            if (level <= MAX_LEVEL) {
                if (timer != null) {
                    timer.cancel();
                }
                showToast("Level up!");
                startLevel();
            } else {
                // Game completed successfully!
                completeChallenge(true);
            }
        } else {
            // Continue current level, spawn new dot to replace
            spawnDot();
        }
    }

    private void onBonusDotClick() {
        if (!gameActive || bonusDot == null) return;

        // Add bonus points
        score += BONUS_POINTS;
        updateScoreText();

        // Show special animation
        showBonusAnimation(bonusDot);

        // Add bonus time
        if (timer != null) {
            timer.cancel();
        }
        timeRemaining += 3000; // 3 second bonus
        startTimer(timeRemaining);

        // Hide bonus dot
        bonusDot.setVisibility(View.GONE);

        showToast("Bonus! +5 points, +3 seconds");
    }

    private void positionDotRandomly(ImageView dot) {
        handler.post(() -> {
            int areaWidth = gameArea.getWidth();
            int areaHeight = gameArea.getHeight();

            if (areaWidth == 0 || areaHeight == 0) {
                // Retry once layout is drawn
                gameArea.postDelayed(() -> positionDotRandomly(dot), 100);
                return;
            }

            int dotSize = dot.getLayoutParams().width;
            int x = random.nextInt(areaWidth - dotSize);
            int y = random.nextInt(areaHeight - dotSize);

            // Ensure dots don't overlap too much
            boolean validPosition = true;
            for (ImageView existingDot : dots) {
                if (existingDot != dot) {
                    float existingX = existingDot.getX();
                    float existingY = existingDot.getY();
                    int existingSize = existingDot.getLayoutParams().width;

                    // Check if too close
                    double distance = Math.sqrt(Math.pow(x - existingX, 2) + Math.pow(y - existingY, 2));
                    if (distance < (dotSize + existingSize) / 1.5) {
                        validPosition = false;
                        break;
                    }
                }
            }

            if (validPosition || dots.size() > 10) { // Skip check if too many dots
                dot.setX(x);
                dot.setY(y);
            } else {
                // Try again
                positionDotRandomly(dot);
            }
        });
    }

    private void startTimer(long milliseconds) {
        timeRemaining = milliseconds;

        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(milliseconds, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerText.setText("Time: " + millisUntilFinished / 1000 + "." + (millisUntilFinished % 1000) / 100 + "s");

                // Change color as time decreases
                if (millisUntilFinished < 5000) {
                    timerText.setTextColor(Color.RED);
                } else if (millisUntilFinished < 10000) {
                    timerText.setTextColor(Color.rgb(255, 165, 0)); // Orange
                } else {
                    timerText.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's Up!");
                timerText.setTextColor(Color.RED);

                completeChallenge(false);
            }
        };
        timer.start();
    }

    private void startDotMovement() {
        if (dotsMoving) return;
        dotsMoving = true;

        for (ImageView dot : dots) {
            animateDotMovement(dot);
        }
    }

    private void animateDotMovement(ImageView dot) {
        if (!gameActive) return;

        int areaWidth = gameArea.getWidth();
        int areaHeight = gameArea.getHeight();
        int dotSize = dot.getLayoutParams().width;

        // Calculate new random position
        int newX = random.nextInt(areaWidth - dotSize);
        int newY = random.nextInt(areaHeight - dotSize);

        // Create movement animation
        ObjectAnimator animX = ObjectAnimator.ofFloat(dot, "x", dot.getX(), newX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(dot, "y", dot.getY(), newY);

        // Set duration based on level (faster as level increases)
        int duration = 3000 - ((level - 1) * 500);
        duration = Math.max(duration, 1000); // Minimum 1 second

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animX, animY);
        animSet.setDuration(duration);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Schedule next movement when done
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (gameActive && dots.contains(dot)) {
                    animateDotMovement(dot);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        // Start animation
        animSet.start();
        dotAnimations.add(animSet);
    }

    private void showTapAnimation(ImageView dot) {
        // Create a temporary view at the dot's location
        ImageView effect = new ImageView(this);
        effect.setImageResource(R.drawable.tap_effect);
        effect.setColorFilter(Color.WHITE);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dot.getLayoutParams().width,
                dot.getLayoutParams().height);
        effect.setLayoutParams(params);
        effect.setX(dot.getX());
        effect.setY(dot.getY());
        effect.setAlpha(0.7f);

        gameArea.addView(effect);

        // Animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(effect, "scaleX", 1f, 1.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(effect, "scaleY", 1f, 1.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(effect, "alpha", 0.7f, 0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(scaleX, scaleY, alpha);
        animSet.setDuration(300);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                gameArea.removeView(effect);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                gameArea.removeView(effect);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animSet.start();
    }

    private void showBonusAnimation(ImageView dot) {
        // Create a text label
        TextView bonusText = new TextView(this);
        bonusText.setText("+" + BONUS_POINTS);
        bonusText.setTextColor(Color.YELLOW);
        bonusText.setTextSize(20);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        bonusText.setLayoutParams(params);
        bonusText.setX(dot.getX());
        bonusText.setY(dot.getY() - 50);

        gameArea.addView(bonusText);

        // Animation
        ObjectAnimator moveY = ObjectAnimator.ofFloat(bonusText, "translationY", 0, -100);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(bonusText, "alpha", 1f, 0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(moveY, alpha);
        animSet.setDuration(1000);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                gameArea.removeView(bonusText);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                gameArea.removeView(bonusText);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animSet.start();
    }

    private void fadeOutView(View view) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(500);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.setAlpha(1f); // Reset alpha for future use
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.GONE);
                view.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        fadeOut.start();
    }

    private void completeChallenge(boolean success) {
        gameActive = false;

        // Cancel animations
        for (Animator anim : dotAnimations) {
            anim.cancel();
        }
        dotAnimations.clear();

        // Show result
        String message = success ?
                "Challenge Complete! Score: " + score :
                "Time's Up! Score: " + score;

        showToast(message);
        ChallengeManager.getInstance().addScore(score);


        // Wait before finishing
        handler.postDelayed(() -> {
            // Proceed to next challenge or game over
            if (ChallengeManager.getInstance().isLastChallenge()){
                startActivity(new Intent(CatchDotActivity.this, GameOver.class));
                finish();
            }

            Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();
            if (next != null) {
                startActivity(new Intent(CatchDotActivity.this, next));
                finish();
            } else {
                // Game complete logic here
                finish();
            }
        }, 1500);
    }

    private void updateScoreText() {
        scoreText.setText("Score: " + score);
    }

    private void updateLevelText() {
        levelText.setText("Level: " + level + "/" + MAX_LEVEL);
    }

    private void updateMultiplierText() {
        multiplierText.setText("x" + scoreMultiplier);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        gameActive = false;

        // Cancel all animations
        for (Animator anim : dotAnimations) {
            anim.cancel();
        }
        dotAnimations.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameActive && timeRemaining > 0) {
            gameActive = true;
            startTimer(timeRemaining);

            // Restart dot movement if needed
            if (level > 1) {
                startDotMovement();
            }
        }
    }
}