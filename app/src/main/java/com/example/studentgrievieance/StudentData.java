// ViewGrievances.java
package com.example.studentgrievieance;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.activity.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class StudentData extends AppCompatActivity {

    private EditText Name, USN, Department, Phone, Sem;
    private DatabaseReference databaseReference;
    private ImageView imageView;


    private FirebaseStorage storage;
    private Button save, update;
    private EditText editTextSuggestion;
    private String complaintId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);
        storage = FirebaseStorage.getInstance();

        // Initialize the TextViews
        Name = findViewById(R.id.name);
        USN = findViewById(R.id.usn);
        Department = findViewById(R.id.department);
        Phone = findViewById(R.id.phone);
        Sem = findViewById(R.id.sem);

        // Initialize the Buttons and EditText
        save = findViewById(R.id.save);
        imageView=findViewById(R.id.image);

        // Initialize the Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("students");

        // Retrieve and display the complaint details
        retrieveComplaintDetails();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the necessary EditText fields
                Name.setEnabled(false);
                Department.setEnabled(false);
                Phone.setEnabled(false);
                Sem.setEnabled(false);

                // Get the updated values from the EditText fields
                String updatedName = Name.getText().toString().trim();
                String updatedDepartment = Department.getText().toString().trim();
                String updatedPhone = Phone.getText().toString().trim();
                String updatedSem = Sem.getText().toString().trim();

                // Update the student details in the Firebase Realtime Database
                DatabaseReference studentRef = databaseReference.child(complaintId);
                studentRef.child("name").setValue(updatedName);
                studentRef.child("department").setValue(updatedDepartment);
                studentRef.child("phone").setValue(updatedPhone);
                studentRef.child("semester").setValue(updatedSem);

                // Show a toast message to indicate successful save
                Toast.makeText(StudentData.this, "Student details saved", Toast.LENGTH_SHORT).show();

                // Change the text of the update button back to "Update"
                update.setText("Update");
            }
        });
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable the necessary EditText fields for editing
                Name.setEnabled(true);
                Department.setEnabled(true);
                Phone.setEnabled(true);
                Sem.setEnabled(true);

                // Change the text of the update button to "Save"
                update.setText("Save");
            }
        });



    }

    private void retrieveComplaintDetails() {
        Student selectedStudent = (Student) getIntent().getSerializableExtra("student");
        if (selectedStudent!= null) {
            complaintId = selectedStudent.getUsn();
            databaseReference.child(complaintId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        // Set the retrieved data to the respective TextViews
                        Name.setText(student.getName());
                        USN.setText(student.getUsn());
                        Department.setText(student.getDepartment());
                        Phone.setText(student.getPhone());
                        Sem.setText(student.getSemester());
                        ;
//
                        String imageId = student.getImageId();
                        StorageReference imageRef = storage.getReference().child("images/" + imageId + ".png");
                        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Convert the image byte array to a Bitmap
                                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                // Set the retrieved image in the ImageView
                                imageView.setImageBitmap(imageBitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If the image retrieval fails, set a default image
                                imageView.setImageResource(R.drawable.profile);
                            }
                        });
                    } else {
                        Toast.makeText(StudentData.this, "Failed to retrieve complaint details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(StudentData.this, "Failed to retrieve complaint details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    }

