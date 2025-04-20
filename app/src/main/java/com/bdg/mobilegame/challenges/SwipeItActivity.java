package com.bdg.mobilegame.challenges;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.R;

public class SwipeItActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private int swipeCount = 0;
    private final int requiredSwipes = 5;
    private TextView swipeCounterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_it);

        Log.d("llll", "i am launched swipeit");


        swipeCounterText = findViewById(R.id.swipeCounterText);
        updateCounter();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        onSwipe();
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        onSwipe();
                    }
                }
                return true;
            }
        });

        Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();

        if (next != null) {
            startActivity(new Intent(this, next));
            //finish();
        } else {
            // Game Over
            //Intent intent = new Intent(this, GameOverActivity.class);
//            intent.putExtra("score", ChallengeManager.getInstance().getScore());
//            startActivity(intent);
//            finish();
        }
    }

    private void onSwipe() {
        swipeCount++;
        updateCounter();
        if (swipeCount >= requiredSwipes) {
            Toast.makeText(this, "Challenge Complete!", Toast.LENGTH_SHORT).show();
            finish(); // End challenge and go back
        }
    }

    private void updateCounter() {
        swipeCounterText.setText("Swipes: " + swipeCount + "/" + requiredSwipes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
