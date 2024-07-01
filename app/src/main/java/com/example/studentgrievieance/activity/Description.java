package com.example.studentgrievieance.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.studentgrievieance.R;

public class Description extends AppCompatActivity {
    private TextView titleTextView,text5;
    private Button submitButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_description);

                // Initialize views
                titleTextView = findViewById(R.id.textView);
                submitButton = findViewById(R.id.button4);
                text5=findViewById(R.id.textView4);
        // Retrieve the content passed from the previous activity
        String content = getIntent().getStringExtra("content");
        String title=getIntent().getStringExtra("title");



        // Set the content in the TextView
        titleTextView.setText(content);
        text5.setText(title);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Description.this,postComplaint.class);
                String[] option=getIntent().getStringArrayExtra("option");
                intent.putExtra("option",option);
                String currentUSN = getIntent().getStringExtra("email");
                intent.putExtra("email",currentUSN);
                startActivity(intent);
                finish();
            }
        });



    }
        }
