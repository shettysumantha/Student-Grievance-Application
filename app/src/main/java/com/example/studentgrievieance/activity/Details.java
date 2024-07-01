package com.example.studentgrievieance.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.studentgrievieance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        final ArrayList<String>list =new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Complaint");
        databaseRef.addValueEventListener(new ValueEventListener() {
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//

          for (DataSnapshot complaintSnapshot : dataSnapshot.getChildren()) {
//                    // Retrieve student details
          Complaint complaint = complaintSnapshot.getValue(Complaint.class);
           String txt=complaint.getName()+""+complaint.getUsn();
           list.add(txt);
           setVisible(true);
//
//
                    }
          adapter.notifyDataSetChanged();
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
            }
        });

    }
}