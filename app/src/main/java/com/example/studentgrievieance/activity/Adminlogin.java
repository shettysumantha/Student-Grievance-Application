package com.example.studentgrievieance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;

public class Adminlogin extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = email.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                String adminEmail = "admin123@gmail.com";
                String adminPassword = "Admin123@";

                if (adminEmail.equals(enteredEmail) && adminPassword.equals(enteredPassword)) {
                    Toast.makeText(Adminlogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Adminlogin.this, AdminDashboard.class));
                    finish();
                } else {
                    Toast.makeText(Adminlogin.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
