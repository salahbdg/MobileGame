package com.bdg.mobilegame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.bdg.mobilegame.GameActivity;

public class GameModeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        Button singlePlayerButton = findViewById(R.id.singlePlayerButton);
        Button multiPlayerButton = findViewById(R.id.multiPlayerButton);

        // Navigate to the respective activity based on user choice
        singlePlayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gameMode", "single");
            startActivity(intent);
        });

        multiPlayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gameMode", "multi");
            startActivity(intent);
        });
    }
}
