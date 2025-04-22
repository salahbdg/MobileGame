package com.bdg.mobilegame;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class GameOver extends Activity {

    MediaPlayer gameOverSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameOverSound = MediaPlayer.create(this, R.raw.closing1);
        gameOverSound.start();

        // Get score from Intent
        int score = getIntent().getIntExtra("score", 0); // 0 is default if not found

        // Set score text (make sure you have a TextView with this ID in your layout)
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Your Score: " + score);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameOverSound != null) {
            gameOverSound.release();
            gameOverSound = null;
        }
    }
}
