package com.bdg.mobilegame.challenges;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bdg.mobilegame.ChallengeManager;
import com.bdg.mobilegame.GameOver;
import com.bdg.mobilegame.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizGameActivity extends AppCompatActivity {

    // UI Elements
    private TextView questionText;
    private TextView categoryText;
    private TextView scoreText;
    private TextView timerText;
    private TextView questionCountText;
    private Button[] answerButtons = new Button[4];

    // Game state
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean canAnswer = true;
    private CountDownTimer questionTimer;
    private static final long QUESTION_TIME_LIMIT = 5000; // 5 seconds
    private static final long DELAY_BETWEEN_QUESTIONS = 1500; // 1.5 seconds

    // Quiz data
    private List<QuizQuestion> quizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);

        Log.d("QuizGameActivity", "Activity launched");

        // Initialize UI elements
        questionText = findViewById(R.id.questionText);
        categoryText = findViewById(R.id.categoryText);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        questionCountText = findViewById(R.id.questionCountText);

        answerButtons[0] = findViewById(R.id.answerButton1);
        answerButtons[1] = findViewById(R.id.answerButton2);
        answerButtons[2] = findViewById(R.id.answerButton3);
        answerButtons[3] = findViewById(R.id.answerButton4);

        // Set click listeners for answer buttons
        for (int i = 0; i < answerButtons.length; i++) {
            final int buttonIndex = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(buttonIndex));
        }

        // Initialize quiz questions
        initializeQuizQuestions();

        // Shuffle questions while maintaining category grouping
        Collections.shuffle(quizQuestions.subList(0, 3));  // Science
        Collections.shuffle(quizQuestions.subList(3, 6));  // Football
        Collections.shuffle(quizQuestions.subList(6, 9));  // Geography
        Collections.shuffle(quizQuestions.subList(9, 12)); // Politics

        // Start the quiz
        updateScoreDisplay();
        loadQuestion(currentQuestionIndex);
    }

    private void initializeQuizQuestions() {
        quizQuestions = new ArrayList<>();

        // Science Questions
        quizQuestions.add(new QuizQuestion(
                "Science",
                "What is the chemical symbol for gold?",
                new String[]{"Au", "Ag", "Fe", "Gd"},
                0
        ));

        quizQuestions.add(new QuizQuestion(
                "Science",
                "Which planet is known as the Red Planet?",
                new String[]{"Jupiter", "Venus", "Mars", "Saturn"},
                2
        ));

        quizQuestions.add(new QuizQuestion(
                "Science",
                "What is the hardest natural substance on Earth?",
                new String[]{"Platinum", "Titanium", "Quartz", "Diamond"},
                3
        ));

        // Football Questions
        quizQuestions.add(new QuizQuestion(
                "Football",
                "Which country won the 2018 FIFA World Cup?",
                new String[]{"Brazil", "Germany", "France", "Argentina"},
                2
        ));

        quizQuestions.add(new QuizQuestion(
                "Football",
                "Who holds the record for most goals in a single calendar year?",
                new String[]{"Cristiano Ronaldo", "Lionel Messi", "Pelé", "Robert Lewandowski"},
                1
        ));

        quizQuestions.add(new QuizQuestion(
                "Football",
                "Which team has won the most UEFA Champions League titles?",
                new String[]{"Liverpool", "AC Milan", "Bayern Munich", "Real Madrid"},
                3
        ));

        // Geography Questions
        quizQuestions.add(new QuizQuestion(
                "Geography",
                "What is the largest country by land area?",
                new String[]{"China", "United States", "Russia", "Canada"},
                2
        ));

        quizQuestions.add(new QuizQuestion(
                "Geography",
                "Which river is the longest in the world?",
                new String[]{"Amazon", "Nile", "Mississippi", "Yangtze"},
                1
        ));

        quizQuestions.add(new QuizQuestion(
                "Geography",
                "What is the capital of Australia?",
                new String[]{"Sydney", "Melbourne", "Canberra", "Perth"},
                2
        ));

        // Politics Questions
        quizQuestions.add(new QuizQuestion(
                "Politics",
                "How many members are in the United Nations Security Council?",
                new String[]{"10", "15", "20", "25"},
                1
        ));

        quizQuestions.add(new QuizQuestion(
                "Politics",
                "Which country has the longest written constitution?",
                new String[]{"United States", "India", "Japan", "Germany"},
                1
        ));

        quizQuestions.add(new QuizQuestion(
                "Politics",
                "Who was the first female Prime Minister of the United Kingdom?",
                new String[]{"Theresa May", "Angela Merkel", "Margaret Thatcher", "Indira Gandhi"},
                2
        ));
    }

    private void loadQuestion(int index) {
        if (index >= quizQuestions.size()) {
            finishQuiz();
            return;
        }

        QuizQuestion currentQuestion = quizQuestions.get(index);

        // Update UI
        questionText.setText(currentQuestion.getQuestion());
        categoryText.setText(currentQuestion.getCategory());
        questionCountText.setText("Question " + (index + 1) + "/" + quizQuestions.size());

        // Set answer options
        String[] options = currentQuestion.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(options[i]);
            answerButtons[i].setBackgroundColor(ContextCompat.getColor(this, R.color.button_normal));
            answerButtons[i].setEnabled(true);
        }

        // Reset state
        canAnswer = true;

        // Start timer
        startQuestionTimer();
    }

    private void startQuestionTimer() {
        timerText.setTextColor(Color.BLACK);

        if (questionTimer != null) {
            questionTimer.cancel();
        }

        questionTimer = new CountDownTimer(QUESTION_TIME_LIMIT, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("%.1fs", millisUntilFinished / 1000.0));

                // Change color as time decreases
                if (millisUntilFinished < 2000) {
                    timerText.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish() {
                timerText.setText("0.0s");
                timerText.setTextColor(Color.RED);
                timeUp();
            }
        };

        questionTimer.start();
    }

    private void onAnswerSelected(int selectedIndex) {
        if (!canAnswer) return;

        canAnswer = false;
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        int correctIndex = currentQuestion.getCorrectAnswerIndex();

        // Highlight the correct answer
        answerButtons[correctIndex].setBackgroundColor(Color.GREEN);

        // If selected answer is wrong, highlight it in red
        if (selectedIndex != correctIndex) {
            answerButtons[selectedIndex].setBackgroundColor(Color.RED);
        } else {
            // Correct answer!
            score += 10;
            updateScoreDisplay();
            Toast.makeText(this, "Correct! +10 points", Toast.LENGTH_SHORT).show();
        }

        // Disable all buttons
        for (Button button : answerButtons) {
            button.setEnabled(false);
        }

        // Proceed to next question after delay
        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        }, DELAY_BETWEEN_QUESTIONS);
    }

    private void timeUp() {
        if (!canAnswer) return;

        canAnswer = false;
        Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show();

        // Highlight the correct answer
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        int correctIndex = currentQuestion.getCorrectAnswerIndex();
        answerButtons[correctIndex].setBackgroundColor(Color.GREEN);

        // Disable all buttons
        for (Button button : answerButtons) {
            button.setEnabled(false);
        }

        // Proceed to next question after delay
        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        }, DELAY_BETWEEN_QUESTIONS);
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + score);
    }

    private void finishQuiz() {
        // Quiz is complete
        Toast.makeText(this, "Quiz Complete! Final Score: " + score, Toast.LENGTH_LONG).show();
        ChallengeManager.getInstance().addScore(score);


        // Wait a moment and then proceed to next challenge
        new Handler().postDelayed(() -> {

            // Start the next activity
            if (ChallengeManager.getInstance().isLastChallenge()){
                Intent intent = new Intent(QuizGameActivity.this, GameOver.class);
                intent.putExtra("score", ChallengeManager.getInstance().getScore()); // or however you store the score
                startActivity(intent);
                finish();
            }

            Class<?> next = ChallengeManager.getInstance().getNextChallengeActivity();
            if (next != null) {
                startActivity(new Intent(QuizGameActivity.this, next));
                finish();
            } else {
                // Handle end of game or return to menu
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (questionTimer != null) {
            questionTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (questionTimer != null) {
            questionTimer.cancel();
        }
    }

    // Inner class to represent a quiz question
    private static class QuizQuestion {
        private final String category;
        private final String question;
        private final String[] options;
        private final int correctAnswerIndex;

        public QuizQuestion(String category, String question, String[] options, int correctAnswerIndex) {
            this.category = category;
            this.question = question;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getCategory() {
            return category;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }
    }
}