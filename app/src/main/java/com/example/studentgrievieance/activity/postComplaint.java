package com.example.studentgrievieance.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class postComplaint extends AppCompatActivity {
    TextInputEditText e1, e2, e3, e4, e7;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    AutoCompleteTextView e5;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");

    ArrayAdapter<String> adapterItems;
    TextInputEditText e6;
    String imageId;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_complaint);
        e1 = findViewById(R.id.name);
        e2 = findViewById(R.id.usn);
        e3 = findViewById(R.id.date);
        e4 = findViewById(R.id.department);
        e5 = findViewById(R.id.category);
        e6 = findViewById(R.id.description);
        String[] option = getIntent().getStringArrayExtra("option");
        e7 = findViewById(R.id.college_name);
        e7.setText("Sahyadri College Of Engineering And Management");
        b1 = findViewById(R.id.button);
        String currentUSN = getIntent().getStringExtra("email");
        e2.setText(currentUSN);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, option);
        e5.setAdapter(adapterItems);
        retrieveProfileInformation();
        e5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item" + item, Toast.LENGTH_SHORT).show();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = e1.getText().toString();
                String usn = e2.getText().toString();
                String d = e3.getText().toString();
                String dpt = e4.getText().toString();
                String c = e5.getText().toString();
                String desc = e6.getText().toString();
                String cn = e7.getText().toString();
                if (d.isEmpty()) {
                    e3.setError("date cannot be empty");
                } else if (c.isEmpty()) {
                    e5.setError("category cannot be empty");
                } else if (desc.isEmpty()) {
                    e6.setError("Description cannot be empty");
                } else {
                    String complaintId = "complaint" + UUID.randomUUID().toString().substring(0, 6);
                    DatabaseReference userComplaintRef = databaseReference.child(usn).child("complaints").child(complaintId);
                    HashMap<String, String> complaintMap = new HashMap<>();
                    complaintMap.put("category", c);
                    complaintMap.put("date", d);
                    complaintMap.put("description", desc);

                    userComplaintRef.setValue(complaintMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Store other details under the usn node of the student
                            DatabaseReference userDetailsRef = databaseReference.child(usn).child("Details");
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", user);
                            userMap.put("USN", usn);
                            userMap.put("Department", dpt);
                            userMap.put("College Name", cn);
                            userMap.put("imageId", imageId);
                            userDetailsRef.setValue(userMap);
                            Toast.makeText(postComplaint.this, "Complaint registered successfully", Toast.LENGTH_SHORT).show();
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Status");
                            DatabaseReference statusRef = databaseReference2.child(usn).child(complaintId);
                            HashMap<String, String>user2= new HashMap<>();
                            user2.put("ComplaintId", complaintId);
                            user2.put("Status","Pending");
                            user2.put("message", "Your complaint is currently pending review");
                            statusRef.setValue(user2).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(postComplaint.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(postComplaint.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            finish();
                        } else {
                            Toast.makeText(postComplaint.this, "Failed to register complaint", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void retrieveProfileInformation() {
        // Initialize Firebase Auth and get the current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize the Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference database = firebaseDatabase.getReference("students");

        String currentUSN = getIntent().getStringExtra("email");
        e2.setText(currentUSN);

        if (currentUSN != null && !currentUSN.isEmpty()) {
            Toast.makeText(postComplaint.this, "", Toast.LENGTH_SHORT).show();

            Query query = database.orderByChild("usn").equalTo(currentUSN);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            // Retrieve the student's data from the snapshot
                            String name = studentSnapshot.child("name").getValue(String.class);
                            String department = studentSnapshot.child("department").getValue(String.class);

                            imageId = studentSnapshot.child("imageId").getValue(String.class);

                            // Set the retrieved data to the respective TextViews
                            e1.setText(name);
                            e4.setText(department);

                            if (imageId != null && !imageId.isEmpty()) {
                                // Retrieve the image from Firebase Storage based on the image ID
                                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageId + ".png");
                                // Add code to handle the retrieved image as per your requirement
                            } else {
                                // If the image ID is empty or null, set a default image
                            }
                        }
                    } else {
                        // Handle the case where the user profile is not found
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that may occur during the database query
                }
            });
        }
    }
}
