package com.example.studentgrievieance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.activity.StudentNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ListView listView;
    private NotificationAdapter adapter;
    private ArrayList<StudentNotification> notificationList;
    private ArrayList<StudentNotification> filteredList;
    EditText searchbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        // Initialize the ListView and ArrayList
        listView = findViewById(R.id.notifyListView);
        notificationList = new ArrayList<>();

        adapter = new NotificationAdapter();
        listView.setAdapter(adapter);

        // Retrieve data from Firebase Realtime Database
        String studentId = getIntent().getStringExtra("email");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Status").child(studentId);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the previous data
                notificationList.clear();

                // Iterate through all the child nodes
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the value of each child node
                    String complaintId = snapshot.getKey();
                    String id=snapshot.child("ComplaintId").getValue(String.class);
                    String status = snapshot.child("Status").getValue(String.class);
                    String msg= snapshot.child("message").getValue(String.class);
                    String suggestion=snapshot.child("suggestion").getValue(String.class);


                    if (complaintId != null && status != null) {
                        StudentNotification notification = new StudentNotification(id, status, msg,suggestion);
                        notificationList.add(notification);
                    }
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
            }
        });

        // Set item click listener for the ListView
    }

    private class NotificationAdapter extends ArrayAdapter<StudentNotification> {

        NotificationAdapter() {
            super(NotificationActivity.this, R.layout.list_notification, notificationList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_notification, parent, false);
            }

           TextView id=convertView.findViewById(R.id.complaintId);
            TextView textViewStatus = convertView.findViewById(R.id.Status);
            TextView mess=convertView.findViewById(R.id.message);
            TextView textViewSuggestion = convertView.findViewById(R.id.Suggestion);

            StudentNotification notification = getItem(position);
            if (notification != null) {
                id.setText("ComplaintId:"+notification.getComplaintId());
                textViewStatus.setText("Status:"+notification.getStatus());
                mess.setText("Message:"+notification.getMessage());
                textViewSuggestion.setText("Suggestion:"+notification.getSuggestion());


            }

            return convertView;
        }
    }

}
