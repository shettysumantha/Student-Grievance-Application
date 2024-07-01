package com.example.studentgrievieance.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentgrievieance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class OtpValidation extends AppCompatActivity {
    private EditText et_sendEmail;
    private TextView resndBtn;
    FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);
        et_sendEmail = findViewById(R.id.email);
        resndBtn = findViewById(R.id.verify);

        firebaseAuth = FirebaseAuth.getInstance();

        resndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_sendEmail.getText().toString();
                if (email.equals("")){
                    Toast.makeText(OtpValidation.this, "Email is empty", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(OtpValidation.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OtpValidation.this,MainActivity.class));
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(OtpValidation.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
