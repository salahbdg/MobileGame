package com.bdg.mobilegame.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.GameOver;
import com.bdg.mobilegame.R;


public class TrueFalse extends AppCompatActivity {

    private TextView statementTextView;
    private TextView timerTextView;
    private TextView scoreTextView;
    private Button trueButton;
    private Button falseButton;

    private int currentScore = 0;
    private int currentQuestionIndex = 0;
    private List<Statement> selectedStatements = new ArrayList<>();
    private CountDownTimer countDownTimer;
    private final long timePerQuestion = 5000; // 5 seconds

    // List of all possible statements
    private final List<Statement> allStatements = new ArrayList<Statement>() {{
        add(new Statement("Java is a purely object-oriented programming language", false));
        add(new Statement("Binary search has a worst-case time complexity of O(log n)", true));
        add(new Statement("HTML is a programming language", false));
        add(new Statement("The first computer programmer was a woman named Ada Lovelace", true));
        add(new Statement("RAM is a type of non-volatile memory", false));
        add(new Statement("SQL stands for Structured Query Language", true));
        add(new Statement("IPv4 uses 32-bit addresses", true));
        add(new Statement("The ACID properties stand for Atomicity, Consistency, Isolation, and Durability", true));
        add(new Statement("Python was created by Guido van Rossum", true));
        add(new Statement("HTTP is a stateful protocol", false));
        add(new Statement("Dijkstra's algorithm finds the shortest path between all pairs of nodes in a graph", false));
        add(new Statement("CSS is a scripting language", false));
        add(new Statement("A compiler translates high-level code to machine code before execution", true));
        add(new Statement("DNS stands for Domain Name System", true));
        add(new Statement("JavaScript was originally called Mocha", true));
        add(new Statement("C++ is a superset of C", false));
        add(new Statement("The first computer bug was an actual moth", true));
        add(new Statement("Git was created by Linus Torvalds", true));
        add(new Statement("TCP guarantees packet delivery", true));
        add(new Statement("The first computers were built in the 1970s", false));
        add(new Statement("Big O notation describes the best-case performance of an algorithm", false));
        add(new Statement("A Turing machine is a theoretical computing machine", true));
        add(new Statement("The term 'bug' in computer science originated in the 2000s", false));
        add(new Statement("Linux is a type of Unix", true));
        add(new Statement("In Java, all parameters are passed by reference", false));
        add(new Statement("HTTPS uses port 443 by default", true));
        add(new Statement("A blockchain is a type of centralized database", false));
        add(new Statement("Wi-Fi stands for Wireless Fidelity", false));
        add(new Statement("In a stack, elements are accessed using FIFO principle", false));
        add(new Statement("Machine learning is a subset of artificial intelligence", true));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        // Initialize UI components
        statementTextView = findViewById(R.id.statementTextView);
        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);

        // Set up button click listeners
        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });



        // Start the game
        startNewGame();
    }

    private void startNewGame() {
        // Reset score and question index
        currentScore = 0;
        currentQuestionIndex = 0;
        scoreTextView.setText("Score: 0");

        // Enable answer buttons
        setAnswerButtonsEnabled(true);



        // Select 5 random statements
        selectedStatements = selectRandomStatements(5);

        // Display first question
        displayQuestion();
    }

    private List<Statement> selectRandomStatements(int count) {
        List<Statement> allStatementsCopy = new ArrayList<>(allStatements);
        Collections.shuffle(allStatementsCopy);
        return allStatementsCopy.subList(0, Math.min(count, allStatementsCopy.size()));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < selectedStatements.size()) {
            Statement currentStatement = selectedStatements.get(currentQuestionIndex);
            statementTextView.setText(currentStatement.getText());

            // Start timer
            startTimer();
        } else {
            // Game over
            endGame();
        }
    }

    private void startTimer() {
        // Cancel any existing timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timePerQuestion, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000 + 1));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0");
                // Time's up, move to next question
                timeUp();
            }
        }.start();
    }

    private void checkAnswer(boolean userAnswer) {
        // Cancel timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Statement currentStatement = selectedStatements.get(currentQuestionIndex);
        boolean isCorrect = userAnswer == currentStatement.isTrue();

        if (isCorrect) {
            // Increase score
            currentScore += 10;
            scoreTextView.setText("Score: " + currentScore);

            // Show correct feedback
            showFeedback(true);
        } else {
            // Show incorrect feedback
            showFeedback(false);
        }

        // Move to next question after a short delay
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing during the delay
            }

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                displayQuestion();
            }
        }.start();
    }

    private void timeUp() {
        // Show feedback that time is up
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Time's Up!")
                .setMessage("You didn't answer in time.")
                .setPositiveButton("Next", (dialog, which) -> {
                    currentQuestionIndex++;
                    displayQuestion();
                })
                .setCancelable(false)
                .show();
    }

    private void showFeedback(boolean isCorrect) {
        String title = isCorrect ? "Correct!" : "Wrong!";
        String message = isCorrect ? "Good job! That's the right answer." : "Sorry, that's incorrect.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Next", (dialog, which) -> {
                    // Dialog dismiss handled by timer
                })
                .setCancelable(false)
                .show();
    }

    private void endGame() {
        // Show final score
        statementTextView.setText("Game Over!");
        timerTextView.setText("");

        // Disable answer buttons
        setAnswerButtonsEnabled(false);
        ChallengeManager.getInstance().addScore(currentScore);
        Toast.makeText(this, "Quiz Complete! Final Score: " + currentScore, Toast.LENGTH_LONG).show();





        // Wait a moment and then proceed to next challenge
        new Handler().postDelayed(() -> {

            // Start the next activity
            if (ChallengeManager.getInstance().isLastChallenge()){
                Intent intent = new Intent(TrueFalse.this, GameOver.class);
                intent.putExtra("score", ChallengeManager.getInstance().getScore()); // or however you store the score
                startActivity(intent);
                finish();
            }

            Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();
            if (next != null) {
                startActivity(new Intent(TrueFalse.this, next));
                finish();
            } else {
                // Handle end of game or return to menu
                finish();
            }
        }, 2000);
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        trueButton.setEnabled(enabled);
        falseButton.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Statement class
    private static class Statement {
        private final String text;
        private final boolean isTrue;

        public Statement(String text, boolean isTrue) {
            this.text = text;
            this.isTrue = isTrue;
        }

        public String getText() {
            return text;
        }

        public boolean isTrue() {
            return isTrue;
        }
    }
}
