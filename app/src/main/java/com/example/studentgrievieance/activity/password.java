package com.example.studentgrievieance.activity;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.studentgrievieance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class password extends AppCompatActivity {
    EditText e1, e2;
    private DatabaseReference databaseReference;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        e1 = findViewById(R.id.editTextTextPersonName);
        e2 = findViewById(R.id.editTextTextPersonName2);
        b1 = findViewById(R.id.button3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = e1.getText().toString();
                String cp = e2.getText().toString();
                if (p.isEmpty()) {
                    e1.setError("password cannot be empty");
                } else if (cp.isEmpty()) {
                    e2.setError("confirm password cannot be empty");
                } else if (!p.equals(cp)) {
                    e2.setError("confirm password is not matched to password");
                } else {

                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference studentsRef = database.getReference("User");

                      // Set the password in the student's USN node
                      //studentsRef.child(phone).child("password").setValue(password);



                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String, String> usermap = new HashMap<>();
                            usermap.put("Password", p);


                            databaseReference.push().setValue(usermap);
                            finish();

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

        });
    }
}
