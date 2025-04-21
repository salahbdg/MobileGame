package com.bdg.mobilegame.challenges;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

//import com.bdg.mobilegame.GameState;
import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.R;

import java.util.Random;

public class CatchDotActivity extends AppCompatActivity {

    private FrameLayout gameArea;
    private ImageView dot;
    private TextView scoreText;
    private int score = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private final int MAX_TAPS = 10;
    private CountDownTimer timer;
    private final long TIME_LIMIT = 10000; // 10 seconds
    private TextView timerText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_dot);

        Log.d("llll", "i am launched catchdot");


        gameArea = findViewById(R.id.gameArea);
        dot = findViewById(R.id.dot);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);


        moveDotRandomly();

        dot.setOnClickListener(v -> {
            score++;
            //GameState.getInstance().addScore(1);
            scoreText.setText("Score: " + score);

            if (score >= MAX_TAPS) {
                finish(); // End challenge
            } else {
                moveDotRandomly();
            }
        });

        timer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's Up!");

                // Add a small delay to allow the user to see the result briefly
                new Handler().postDelayed(() -> {
                    // Start the next activity
                    Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();

                    if (next != null) {
                        startActivity(new Intent(CatchDotActivity.this, next));
                        finish();
                    }
                }, 500); // 1.5 seconds delay

            }
        };
        timer.start();

//        Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();
//
//        Log.d(
//                "nextactivity", next.getName()
//        );
//
//        if (next != null) {
//            startActivity(new Intent(this, next));
//            finish();
//        } else {
//            // Game Over
//            //Intent intent = new Intent(this, GameOverActivity.class);
////            intent.putExtra("score", ChallengeManager.getInstance().getScore());
////            startActivity(intent);
////            finish();
//        }
    }




//    Helper methods


    private void moveDotRandomly() {
        handler.post(() -> {
            int areaWidth = gameArea.getWidth();
            int areaHeight = gameArea.getHeight();

            if (areaWidth == 0 || areaHeight == 0) {
                // Retry once layout is drawn
                gameArea.postDelayed(this::moveDotRandomly, 100);
                return;
            }

            int dotSize = dot.getWidth();
            int x = random.nextInt(areaWidth - dotSize);
            int y = random.nextInt(areaHeight - dotSize);

            dot.setX(x);
            dot.setY(y);
        });
    }

}
