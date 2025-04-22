package com.bdg.mobilegame;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bdg.mobilegame.challenges.CatchDotActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private String gameMode;
    private GridView challengesGrid;
    private BluetoothAdapter bluetoothAdapter;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_ENABLE_BT = 1001;


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
        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(new Challenge("Shake It!", "sensor", 10, com.bdg.mobilegame.challenges.ShakeIt.class)); // ADD SOUND EFFECT
        challengeList.add(new Challenge("Catch the Dot", "motion", 10, com.bdg.mobilegame.challenges.CatchDotActivity.class));
        challengeList.add(new Challenge("Swipe It!", "motion", 10, com.bdg.mobilegame.challenges.SwipeItActivity.class));
        challengeList.add(new Challenge("Swipe It!", "motion", 10, com.bdg.mobilegame.challenges.QuizGameActivity.class));
        challengeList.add(new Challenge("Swipe It!", "motion", 10, com.bdg.mobilegame.challenges.TrueFalse.class));


        // Add 3 more as you build them

        //shuffle then set challenges for Challenge manager
        Collections.shuffle(challengeList);
        ChallengeManager.getInstance().setChallenges(challengeList);

        Class<?> firstActivity = ChallengeManager.getInstance().getCurrentChallenge().getActivityToLaunch();
        if (firstActivity != null) {
            Intent intent = new Intent(this, firstActivity);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    private void startMultiPlayerGame() {
        // Logic for starting multiplayer game
        Toast.makeText(this, "Starting Multiplayer Game", Toast.LENGTH_SHORT).show();

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        // Launch MultiplayerActivity
        Intent intent = new Intent(GameActivity.this, MultiplayerActivity.class);
        startActivity(intent);
        finish();  // End the current activity after starting the next one
    }
}
