package com.bdg.mobilegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bdg.mobilegame.challenges.TapFast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MultiplayerActivity extends AppCompatActivity {

    private  Button resetBtn;

    private DatabaseReference counterRef;
    private int currentValue = 0;
    private TextView role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer);

        resetBtn = findViewById(R.id.resetBtn);


        FirebaseApp.initializeApp(this);


        // Reference to the counter in Firebase
        counterRef = FirebaseDatabase.getInstance().getReference("shared_counter");

        // test db connection
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean connected = snapshot.getValue(Boolean.class);
                        Toast.makeText(MultiplayerActivity.this, connected ? "Connected" : "Disconnected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MultiplayerActivity.this, "Connection listener cancelled", Toast.LENGTH_SHORT).show();
                    }
                });





        // Reset counter
        resetBtn.setOnClickListener(view ->{
            counterRef.setValue(0);
        });

        Button incrementer = findViewById(R.id.incrementer);
        incrementer.setOnClickListener(v ->{
            Intent intent = new Intent(MultiplayerActivity.this, TapFast.class);
            intent.putExtra("role","incrementer");
            startActivity(intent);
        });

        Button decrementer = findViewById(R.id.decrementer);
        decrementer.setOnClickListener(v ->{
            Intent intent = new Intent(MultiplayerActivity.this, TapFast.class);
            intent.putExtra("role","decrementer");
            startActivity(intent);
        });
    }
}