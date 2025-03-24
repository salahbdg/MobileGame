package com.bdg.mobilegame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private String gameMode;
    private GridView challengesGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Retrieve the game mode passed from GameModeActivity
        Intent intent = getIntent();
        gameMode = intent.getStringExtra("gameMode");

        // Find the TextView where the game mode will be displayed
        TextView gameModeText = findViewById(R.id.gameModeText);

        // start challenge button
        Button startChallenge = findViewById(R.id.startChallengeButton);

        // Set the game mode text
        if (gameMode != null) {
            gameModeText.setText("Game Mode: " + gameMode);

            // Start the game based on the selected mode
            switch (gameMode) {
                case "single":
                    startChallenge.setOnClickListener(v -> {
                        startSinglePlayerGame();
                    });                    break;
                case "multi":
                    startMultiPlayerGame();
                    break;
                default:
                    Toast.makeText(this, "Invalid Game Mode", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        // Initialize the GridView
        challengesGrid = findViewById(R.id.challengesGrid);

        // Define a list of challenge images (this can be changed to reflect your actual challenge images)
        int[] challengeImages = {
                R.drawable.v1,
                R.drawable.v2,
                R.drawable.v3,
                R.drawable.v4,
                R.drawable.v5,
                R.drawable.v6
        };

        // Set the adapter for the GridView
        ChallengesAdapter adapter = new ChallengesAdapter(this, challengeImages);
        challengesGrid.setAdapter(adapter);
    }

    private void startSinglePlayerGame() {
        // Logic for starting single-player game
        Toast.makeText(this, "Starting Single Player Game", Toast.LENGTH_SHORT).show();

        // Launch SinglePlayerActivity
        Intent intent = new Intent(GameActivity.this, SinglePlayerActivity.class);
        startActivity(intent);
        finish();  // End the current activity after starting the next one
    }

    private void startMultiPlayerGame() {
        // Logic for starting multiplayer game
        Toast.makeText(this, "Starting Multiplayer Game", Toast.LENGTH_SHORT).show();

        // Launch MultiplayerActivity
        Intent intent = new Intent(GameActivity.this, MultiplayerActivity.class);
        startActivity(intent);
        finish();  // End the current activity after starting the next one
    }
}
