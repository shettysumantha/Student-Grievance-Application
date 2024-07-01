package com.example.studentgrievieance.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Profile extends AppCompatActivity {

    private TextView nameTextView, usnTextView, departmentTextView, phoneTextView;
    private ImageView profileImageView;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.name2);
        usnTextView = findViewById(R.id.usntext);
        departmentTextView = findViewById(R.id.departmenttext);
        phoneTextView = findViewById(R.id.phonetext);
        profileImageView = findViewById(R.id.profileImageView);

        // Initialize Firebase Auth and get the current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize the Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("students");

        // Retrieve and display the student's profile information
        retrieveProfileInformation();
    }

    private void retrieveProfileInformation() {
        String currentUSN = getIntent().getStringExtra("email");
        usnTextView.setText(currentUSN);

        if (currentUSN != null && !currentUSN.isEmpty()) {
            Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();

            Query query = databaseReference.orderByChild("usn").equalTo(currentUSN);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            // Retrieve the student's data from the snapshot
                            String name = studentSnapshot.child("name").getValue(String.class);
                            String department = studentSnapshot.child("department").getValue(String.class);
                            String phone = studentSnapshot.child("phone").getValue(String.class);
                            String imageId = studentSnapshot.child("imageId").getValue(String.class);

                            // Set the retrieved data to the respective TextViews
                            nameTextView.setText(name);
                            departmentTextView.setText(department);
                            phoneTextView.setText(phone);

                            if (imageId != null && !imageId.isEmpty()) {
                                // Retrieve the image from Firebase Storage based on the image ID
                                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageId + ".png");
//                                if(imageId.equals(imageRef)) {
//                                    Toast.makeText(Profile.this, "sucessssful", Toast.LENGTH_SHORT).show();
                                    imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            // Convert the image byte array to a Bitmap
                                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            // Set the retrieved image in the ImageView
                                            profileImageView.setImageBitmap(imageBitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // If the image retrieval fails, set a default image
                                            Log.e("ProfileActivity", "Failed to retrieve image from Firebase Storage", e);
                                            profileImageView.setImageResource(R.drawable.profile);
                                        }
                                    });
                                }
                            else {
                                // If the image ID is empty or null, set a default image
                                profileImageView.setImageResource(R.drawable.profile);
                            }
                        }
                    } else {
                        Toast.makeText(Profile.this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that may occur during the database query
                }
            });
        }
    }
    private Bitmap convertBase64ToImage(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
