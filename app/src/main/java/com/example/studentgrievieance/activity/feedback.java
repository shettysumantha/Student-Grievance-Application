package com.example.studentgrievieance.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.studentgrievieance.NotificationActivity;
import com.example.studentgrievieance.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class feedback extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView commentTextView;
    private Button submitButton;

    private DatabaseReference feedbackRef;

    private static final String CHANNEL_ID = "feedback_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ratingBar = findViewById(R.id.ratingBar);
        commentTextView = findViewById(R.id.description);
        submitButton = findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        feedbackRef = FirebaseDatabase.getInstance().getReference("feedback");


    }

    private void submitFeedback() {
        float rating = ratingBar.getRating();
        String comment = commentTextView.getText().toString().trim();

        if (comment.isEmpty()) {
            Toast.makeText(this, "Please provide a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = feedbackRef.push().getKey();
        FeedbackClass feedback = new FeedbackClass(feedbackId, rating, comment);
        feedbackRef.child(feedbackId).setValue(feedback);

        // Send notification message
        sendNotification();

        Toast.makeText(this, "Feedback sent successfully", Toast.LENGTH_SHORT).show();

        // Clear input fields
        ratingBar.setRating(0);
        commentTextView.setText("");
    }


    @SuppressLint("MissingPermission")
    private void sendNotification() {
        // Create a new intent to open the NotificationActivity
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.feedback)
                .setContentTitle("Feedback Submitted")
                .setContentText("Thank you for your feedback!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

}
