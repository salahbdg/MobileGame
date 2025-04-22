package com.bdg.mobilegame.challenges;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bdg.mobilegame.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TapFast extends AppCompatActivity {
    private String role;
    private DatabaseReference counterRef;
    private int currentValue = 0;
    private TextView counterTextView;
    private LinearLayout rootLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapfast);

        counterTextView = findViewById(R.id.counterTextView);
        rootLayout = findViewById(R.id.rootLayout);



        role = getIntent().getStringExtra("role");
        FirebaseApp.initializeApp(this);
        counterRef = FirebaseDatabase.getInstance().getReference("shared_counter");

        // test db connection
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean connected = snapshot.getValue(Boolean.class);
                        Toast.makeText(TapFast.this, connected ? "Connected" : "Disconnected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TapFast.this, "Connection listener cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                if (value != null) {
                    currentValue = value;
                    counterTextView.setText("Counter: " + currentValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TapFast.this, "Failed to read value", Toast.LENGTH_SHORT).show();
            }
        });





        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle screen tap here
                Log.d("TAP", "Whole screen tapped!");
                if (role.equals("incrementer")){
                    counterRef.setValue(currentValue + 1);
                } else if (role.equals("decrementer")){
                    counterRef.setValue(currentValue - 1);

                }
            }
        });
    }
}