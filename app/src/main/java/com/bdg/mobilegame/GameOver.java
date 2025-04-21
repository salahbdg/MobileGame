package com.bdg.mobilegame;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class GameOver extends Activity {

    MediaPlayer gameOverSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameOverSound = MediaPlayer.create(this, R.raw.closing1);
        gameOverSound.start();
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
