package com.example.studentgrievieance.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studentgrievieance.R;

public class welcome extends AppCompatActivity {
     TextView tx1;
     ImageView im;
     private static int Splash_timeout=6000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        tx1=findViewById(R.id.textView2);
        im=findViewById(R.id.imageView2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashintent=new Intent(welcome.this, Home.class);
                startActivity(splashintent);
                finish();
            }
        },Splash_timeout);
        Animation animation= AnimationUtils.loadAnimation(welcome.this,R.anim.animation1);
        tx1.startAnimation(animation);
        Animation animation2= AnimationUtils.loadAnimation(welcome.this,R.anim.animation2);
        im.startAnimation(animation2);
    }
}