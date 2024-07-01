package com.example.studentgrievieance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;

public class ViewStudentDetails extends AppCompatActivity {

    private ListView listView;
    private StudentAdapter adapter;
    private ArrayList<Student> studentList;
    private FirebaseStorage storage;
    private ArrayList<Student> filteredList;
    EditText searchbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_details);
        storage = FirebaseStorage.getInstance();

        // Initialize the ListView and ArrayList
        listView = findViewById(R.id.studentListView);
        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new StudentAdapter();
        listView.setAdapter(adapter);
        searchbar = findViewById(R.id.search);
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                filterList(query);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Retrieve data from Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("students");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the previous data
                studentList.clear();

                // Iterate through all the child nodes
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the value of each child node
                    Student student = snapshot.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                filterList("");
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
            }
        });

        // Set item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = studentList.get(position);
                Intent intent = new Intent(ViewStudentDetails.this, StudentData.class);
                intent.putExtra("student", student);
                startActivity(intent);
            }
        });
    }

    private class StudentAdapter extends ArrayAdapter<Student> {

        StudentAdapter() {
            super(ViewStudentDetails.this, R.layout.list_item_student, studentList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_student, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.image);
            TextView textViewName = convertView.findViewById(R.id.Name);
            TextView textViewUSN = convertView.findViewById(R.id.USN);

            Student student = getItem(position);
            if (student != null) {
                textViewName.setText(student.getName());
                textViewUSN.setText(student.getUsn());
                String imageId = student.getImageId();

                // Get a reference to the image file in Firebase Storage
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
            }

            return convertView;
        }
    }

    private void filterList(String query) {
        filteredList.clear();
        for (Student student : studentList) {
            if (student.getName().toLowerCase().contains(query) ||
                    student.getUsn().toLowerCase().contains(query)) {
                filteredList.add(student);
            }
        }

        // Update the studentList with the filteredList
        studentList.clear();
        studentList.addAll(filteredList);
    }
}

