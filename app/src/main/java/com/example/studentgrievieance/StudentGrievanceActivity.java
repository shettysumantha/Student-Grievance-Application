package com.example.studentgrievieance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.activity.Complaint;
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
import java.util.HashSet;

public class StudentGrievanceActivity extends AppCompatActivity {

    private ListView listView;
    private ComplaintAdapter adapter;
    private ArrayList<Complaint> complaintList;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private HashSet<String> uniqueComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        storage = FirebaseStorage.getInstance();

        // Initialize the ListView and ArrayList
        listView = findViewById(R.id.listView3);
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter();
        listView.setAdapter(adapter);

        // Retrieve data from Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
        uniqueComplaints = new HashSet<>();
        // ...
        // ...
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the previous data
                complaintList.clear();
                uniqueComplaints.clear();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String user = studentSnapshot.getKey();
                    DatabaseReference userDetailsRef = databaseReference.child(user).child("Details");

                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Retrieve the student's details from the snapshot
                                String name = snapshot.child("name").getValue(String.class);
                                String usn=snapshot.child("USN").getValue(String.class);
                                String image = snapshot.child("imageId").getValue(String.class);

                                // Iterate through the complaints of the student
                                for (DataSnapshot complaintSnapshot : studentSnapshot.child("complaints").getChildren()) {
                                    // Retrieve the value of each complaint
                                    String category = complaintSnapshot.child("category").getValue(String.class);
                                    String date = complaintSnapshot.child("date").getValue(String.class);
                                    String description = complaintSnapshot.child("description").getValue(String.class);

                                    // Create a new Complaint object
                                    Complaint complaint = new Complaint();
                                    complaint.setName(name);
                                    complaint.setImageId(image);
                                    complaint.setCategory(category);
                                    complaint.setUsn(usn);
                                    complaint.setDate(date);
                                    complaint.setDescription(description);
                                    String complaintId = complaintSnapshot.getKey();
                                    complaint.setComplaintId(complaintId);

                                    // Add the complaint to the list
                                    complaintList.add(complaint);
                                }
                            }

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(StudentGrievanceActivity.this,"failed",Toast.LENGTH_SHORT).show();
                            // Handle error case
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
            }
         });
// ...


        // Set item click listener for the ListView
        // Inside StudentGrievanceActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Complaint complaint = complaintList.get(position);
                Intent intent = new Intent(StudentGrievanceActivity.this, ViewGrievances.class);
                intent.putExtra("complaint", complaint); // Pass the complaint object
                startActivity(intent);
            }
        });

    }

    private boolean isDuplicateComplaint(Complaint complaint) {
        String complaintId = complaint.getComplaintId();
        String usn = complaint.getUsn();
        String key = usn + "-" + complaintId;
        if (uniqueComplaints.contains(key)) {
            return true;
        } else {
            uniqueComplaints.add(key);
            return false;
        }
    }

    private class ComplaintAdapter extends ArrayAdapter<Complaint> {
        ComplaintAdapter() {
            super(StudentGrievanceActivity.this, R.layout.list_item_complaint, complaintList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_complaint, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.imageView);
            TextView textViewName = convertView.findViewById(R.id.textViewName);

            Complaint complaint = getItem(position);
            if (complaint != null) {
                textViewName.setText(complaint.getName());

                String imageId = complaint.getImageId();
                if (imageId != null && !imageId.isEmpty()) {
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
                } else {
                    // If the image ID is empty or null, set a default image
                    imageView.setImageResource(R.drawable.profile);
                }
            }

            return convertView;
        }

    }
}
