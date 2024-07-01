package com.example.studentgrievieance.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentgrievieance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.xml.namespace.QName;

public class MainActivity extends AppCompatActivity {
 private FirebaseAuth auth;
 private EditText email;
 private EditText password;
 private TextView signuprt,f;
 private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.button);
        signuprt=findViewById(R.id.loginrt);
        f=findViewById(R.id.forgot);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("User");
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {

                            // Retrieve the email and password from the snapshot
                            String email = adminSnapshot.child("USN").getValue(String.class);

                            String password = adminSnapshot.child("password").getValue(String.class);
                            // Compare the retrieved data with user input
                            if (email != null && password != null && email.equals(user) && password.equals(pass)) {

                                Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainActivity.this, dashboard.class);
                                intent.putExtra("email",email);
                                startActivity(intent);
                                finish();


                            } else {
                                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                    // Login successful, proceed to the next activity or perform necessary actions
                    //Toast.makeText(Adminlogin.this, "Login failed", Toast.LENGTH_SHORT).show();


                    // Proceed to the next activity or perform necessary actions


                    // Login failed, show an error message or take appropriate action


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                                                 // Handle any errors
                    }
                });
            }
        });
        signuprt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OtpValidation.class));
            }
        });
    }
}







