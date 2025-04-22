package com.bdg.mobilegame.challenges;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.GameOver;
import com.bdg.mobilegame.R;
import com.bdg.mobilegame.utils.ShakeDetector;
import com.bdg.mobilegame.utils.ShakeListener;

public class ShakeIt extends Activity implements ShakeListener {

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private int shakeCount = 0;
    private boolean isChallengeRunning = false;

    private TextView counterText, timerText;
    private CountDownTimer timer;
    //private DatabaseReference counterRef;

    private final int MAX_SHAKES = 300;
    private final long TIME_LIMIT = 10000; // 10 seconds
    private MediaPlayer shakeSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_it_activity);

        Log.d("llll", "i am launched shakeit");

        shakeSound = MediaPlayer.create(this, R.raw.shake_sound); // Make sure shake_sound.mp3 is in res/raw


        counterText = findViewById(R.id.shakeCounterText);
        timerText = findViewById(R.id.timerText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);

        startChallenge();




    }

    // Helper methods
    private void startChallenge() {
        shakeCount = 0;
        counterText.setText("Shakes: 0");
        isChallengeRunning = true;

        timer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                isChallengeRunning = false;
                timerText.setText("Time's Up!");
                showResult();
                ChallengeManager.getInstance().addScore(shakeCount);

                // Add a small delay to allow the user to see the result briefly
                new Handler().postDelayed(() -> {
                    // Start the next activity
                    if (ChallengeManager.getInstance().isLastChallenge()){
                        Intent intent = new Intent(ShakeIt.this, GameOver.class);
                        intent.putExtra("score", ChallengeManager.getInstance().getScore()); // or however you store the score
                        startActivity(intent);
                        finish();
                    }

                    Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();
                    if (next != null) {
                        startActivity(new Intent(ShakeIt.this, next));
                        finish();
                    }
                }, 500); // 1.5 seconds delay

            }
        };
        timer.start();
    }

    private void showResult() {
        String result = (shakeCount >= MAX_SHAKES) ? "Good!" : "NEXT";
        Toast.makeText(this, result + " Total Shakes: " + shakeCount, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(
                shakeDetector,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shakeSound != null) {
            shakeSound.release();
            shakeSound = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
        if (timer != null) timer.cancel();
    }

    @Override
    public void onShake() {
        if (isChallengeRunning) {
            shakeCount++;

            // Play sound
            if (shakeSound != null) {
                shakeSound.start();
            }

            runOnUiThread(() -> counterText.setText("Shakes: " + shakeCount));
        }
    }
}
