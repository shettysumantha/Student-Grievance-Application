package com.example.studentgrievieance.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;
import com.example.studentgrievieance.StudentGrievanceActivity;
import com.example.studentgrievieance.ViewStudentDetails;

public class AdminDashboard extends AppCompatActivity {
    Button c1,c2,c3,c4;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        c1=findViewById(R.id.add);
        c4=findViewById(R.id.update);
        c2=findViewById(R.id.view);
        c3=findViewById(R.id.logout);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, AddStudentActivity.class));
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, StudentGrievanceActivity.class));
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, Adminlogin.class));
                finish();
            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, ViewStudentDetails.class));
            }
        });




    }
}