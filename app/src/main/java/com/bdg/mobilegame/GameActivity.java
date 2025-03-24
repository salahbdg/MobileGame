package com.bdg.mobilegame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private String gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Retrieve the game mode passed from GameModeActivity
        Intent intent = getIntent();
        gameMode = intent.getStringExtra("gameMode");

        // start challenge button
        Button startChallenge = findViewById(R.id.startChallengeButton);

        // Find the TextView where the game mode will be displayed
        TextView gameModeText = findViewById(R.id.gameModeText);

        // Start the game based on the selected mode
        if (gameMode != null) {
            gameModeText.setText("Game Mode: " + gameMode);

            switch (gameMode) {
                case "single":
                    startChallenge.setOnClickListener(v -> {
                        startSinglePlayerGame();
                    });
                    break;
                case "multi":
                    startMultiPlayerGame();
                    break;
                default:
                    Toast.makeText(this, "Invalid Game Mode", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void startSinglePlayerGame() {
        // Logic for single-player game
        Toast.makeText(this, "Starting Single Player Game", Toast.LENGTH_SHORT).show();
        // Proceed with setting up the single-player game (challenges, scores, etc.)

        // Launch SinglePlayerActivity
        Intent intent = new Intent(GameActivity.this, SinglePlayerActivity.class);
        startActivity(intent);
        finish();  // End the current activity after starting the next one
    }

    private void startMultiPlayerGame() {
        // Logic for multiplayer game
        Toast.makeText(this, "Starting Multiplayer Game", Toast.LENGTH_SHORT).show();
        // Proceed with multiplayer setup (connecting, synchronizing devices, etc.)
    }
}
