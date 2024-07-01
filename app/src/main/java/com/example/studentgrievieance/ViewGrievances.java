package com.example.studentgrievieance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.activity.Complaint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ViewGrievances extends AppCompatActivity {
    private TextView textViewName, textViewUSN, textViewDepartment, textViewCategory, textViewDescription, textViewDate;
    private ImageView imageView;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private String complaintId;
    private Button buttonAccept, buttonReject, buttonReply, nextButton;
    private EditText editTextSuggestion;
    private String usn;
    private Complaint complaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grievances);
        storage = FirebaseStorage.getInstance();

        // Initialize the TextViews
        textViewName = findViewById(R.id.textViewName);
        textViewUSN = findViewById(R.id.textViewUSN);
        textViewDepartment = findViewById(R.id.textViewDepartment);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewDate = findViewById(R.id.textViewDate);

        // Initialize the Buttons and EditText
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonReject = findViewById(R.id.buttonReject);
        buttonReply = findViewById(R.id.buttonReply);
        editTextSuggestion = findViewById(R.id.editTextSuggestion);
        imageView = findViewById(R.id.imageView);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");
        Complaint selectedComplaint = (Complaint) getIntent().getSerializableExtra("complaint");
        complaintId = selectedComplaint.getComplaintId();

        // Retrieve the complaint object from the intent
        displayComplaintDetails(complaint);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateComplaintStatus("Accepted", "We have received your complaint and it is currently being addressed. Thank you for bringing this to our attention.");
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateComplaintStatus("Rejected", "After careful review, we regret to inform you that your complaint has been found invalid. We appreciate your understanding.");
            }
        });

        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suggestion = editTextSuggestion.getText().toString().trim();
                if (!suggestion.isEmpty()) {
                    // Implement the logic to send the suggestion as a reply
                    // For example, you can save the suggestion in the database or send it as a notification

                    Toast.makeText(ViewGrievances.this, "Reply sent: " + suggestion, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewGrievances.this, "Please enter a suggestion", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the status as "Pending" if no action is taken

    }

    private void displayComplaintDetails(Complaint complaint) {
        Complaint selectedComplaint = (Complaint) getIntent().getSerializableExtra("complaint");
        if (selectedComplaint != null) {
            complaintId = selectedComplaint.getComplaintId();
            usn = selectedComplaint.getUsn();
            databaseReference.child(usn).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    if (complaint != null) {
                        textViewName.setText("Name:" + complaint.getName());
                        textViewUSN.setText("USN:" + complaint.getUsn());
                        textViewDepartment.setText("Department:" + complaint.getDepartment());

                        String imageId = complaint.getImageId();
                        if (imageId != null && !imageId.isEmpty()) {
                            // Get a reference to the image file in Firebase Storage
                            StorageReference imageRef = storage.getReference().child("images/" + imageId + ".png");

                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                                // Convert the image byte array to a Bitmap
                                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                // Set the retrieved image in the ImageView
                                imageView.setImageBitmap(imageBitmap);
                            }).addOnFailureListener(e -> {
                                // If the image retrieval fails, set a default image
                                imageView.setImageResource(R.drawable.profile);
                            });
                        } else {
                            // If the image ID is empty or null, set a default image
                            imageView.setImageResource(R.drawable.profile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.child(usn).child("complaints").child(complaintId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    if (complaint != null) {
                        textViewCategory.setText("Grievance:" + complaint.getCategory());
                        textViewDescription.setText("Description:" + complaint.getDescription());
                        textViewDate.setText("Date:" + complaint.getDate());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void updateComplaintStatus(String status, String message) {
        // Update the complaint status and message in the database
        Complaint selectedComplaint = (Complaint) getIntent().getSerializableExtra("complaint");
        complaintId = selectedComplaint.getComplaintId();
        usn = selectedComplaint.getUsn();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Status");
        DatabaseReference statusRef = databaseReference.child(usn).child(complaintId);

        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> statusMap = new HashMap<>();
                statusMap.put("status", status);
                String storedStatus = dataSnapshot.child("Status").getValue(String.class);
                if (storedStatus != null && storedStatus.equals("Pending")) {

                    // Set the updated status
                    statusRef.child("ComplaintId").setValue(complaintId);
                    statusRef.child("Status").setValue(status);
                    statusRef.child("message").setValue(message);
                }
                else
                {
                    Toast.makeText(ViewGrievances.this, "Status already exist ", Toast.LENGTH_SHORT).show();
                }

                String updatedMessage = message; // Declare the message variable here

                if (!dataSnapshot.hasChild("message")) {
                    // Set the default pending message only if it doesn't exist
                    if (status.equals("Accepted") || status.equals("Rejected")) {
                        statusMap.put("Complaint Id", complaintId);
                        statusMap.put("Status", status);
                        statusMap.put("message", message);
                    } else {
                        updatedMessage = "Your complaint is currently pending review";
                        statusMap.put("message", updatedMessage);
                        statusRef.child("message").setValue(updatedMessage);
                    }
                }

                // Disable the accept and reject buttons
                buttonAccept.setEnabled(false);
                buttonReject.setEnabled(false);

                // Show a toast message indicating the status update
                Toast.makeText(ViewGrievances.this, "Complaint " + status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error case
            }
        });

        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suggestion = editTextSuggestion.getText().toString().trim();
                if (!suggestion.isEmpty()) {
                    // Implement the logic to send the suggestion as a reply
                    // For example, you can save the suggestion in the database or send it as a notification
                    DatabaseReference suggestionRef = statusRef.child("suggestion");
                    suggestionRef.setValue(suggestion);
                    Toast.makeText(ViewGrievances.this, "Reply sent: " + suggestion, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewGrievances.this, "Please enter a suggestion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
