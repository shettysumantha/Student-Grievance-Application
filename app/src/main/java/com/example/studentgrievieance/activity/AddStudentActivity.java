package com.example.studentgrievieance.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;
import com.example.studentgrievieance.ViewStudentDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nameEditText, usnEditText, phoneEditText, departmentEditText, semEditText;
    private Button saveButton, viewButton;
    private ImageView studentImageView;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        nameEditText = findViewById(R.id.name);
        usnEditText = findViewById(R.id.usn);
        phoneEditText = findViewById(R.id.phone);
        departmentEditText = findViewById(R.id.department);
        semEditText = findViewById(R.id.sem);
        saveButton = findViewById(R.id.button);
        studentImageView = findViewById(R.id.studentImage);
        viewButton = findViewById(R.id.btn2);

        // Initialize the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStudentActivity.this, ViewStudentDetails.class));
            }
        });

        // Set an onClickListener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String usn = usnEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String department = departmentEditText.getText().toString().trim();
                String semester = semEditText.getText().toString().trim();

                if (name.isEmpty()) {
                    nameEditText.setError("Name cannot be empty");
                } else if (usn.isEmpty()) {
                    usnEditText.setError("USN cannot be empty");
                } else if (phone.isEmpty()) {
                    phoneEditText.setError("Phone cannot be empty");
                } else if (department.isEmpty()) {
                    departmentEditText.setError("Department cannot be empty");
                } else if (semester.isEmpty()) {
                    semEditText.setError("Semester cannot be empty");
                } else {
                    // Get the image drawable from ImageView
                    BitmapDrawable drawable = (BitmapDrawable) studentImageView.getDrawable();
                    Bitmap imageBitmap = drawable.getBitmap();

                    // Convert the image to a Base64 string
                    String imageBase64 = convertImageToBase64(imageBitmap);

                    // Generate a unique image ID
                    String imageId = UUID.randomUUID().toString();

                    // Store the image in Firebase Storage
                    storeImageInFirebaseStorage(imageBase64, imageId);

                    // Create a student object
                    Student student = new Student(name, usn, phone, department, semester, imageId);

                    // Store the student object in Firebase Realtime Database
                    saveStudentToDatabase(usn, student);

                    // Clear the input fields
                    clearInputFields();

                    Toast.makeText(AddStudentActivity.this, "Student data saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        studentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                studentImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void storeImageInFirebaseStorage(String imageBase64, String imageId) {
        if (imageBase64.isEmpty()) {
            // No image selected, do not save the imageId
            return;
        }

        // Replace "your-storage-bucket" with your actual Firebase Storage bucket URL
        String storageBucket = "gs://studentgrievieance.appspot.com";
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageBucket);

        // Create a reference to the image file with the specified imageId
        StorageReference imageRef = storageRef.child("images/" + imageId + ".png");

        // Convert the Base64 string back to bytes
        byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);

        // Upload the image bytes to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageBytes);
    }

    private void saveStudentToDatabase(String usn, Student student) {
        // Save the student object under the "students" node with the specified USN
        databaseReference.child(usn).setValue(student);
    }

    private void clearInputFields() {
        nameEditText.setText("");
        usnEditText.setText("");
        phoneEditText.setText("");
        departmentEditText.setText("");
        semEditText.setText("");
        studentImageView.setImageResource(R.drawable.profile);
    }
}
