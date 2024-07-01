package com.example.studentgrievieance.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentgrievieance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {

    private EditText mobileNumberEditText;
    private boolean otpSent = false;
    private Button sendOTPButton;
    private EditText otpEditText, usn, password, confirmPassword;
    private Button verifyOTPButton, b7;
    private TextView statusTextView;
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
    private static SecureRandom random = new SecureRandom();
    private DatabaseReference databaseReference;
    private String mobileNumber;
    private String verificationCode;
    private String countryCode = "+91";
    private String id = "";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


//    private DatabaseReference databaseReference;
//    private String mobileNumber;
//    private String verificationCode;
//    private String countrycode="+91";
//    private String id="";
//    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usn = findViewById(R.id.usnEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        sendOTPButton = findViewById(R.id.sendOTPButton);

        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.cpassword);
        b7 = findViewById(R.id.button7);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = usn.getText().toString().trim();
                String p = mobileNumberEditText.getText().toString().trim();
                if (user.isEmpty()) {
                    usn.setError("usn cannot be empty");
                } else if (p.isEmpty()) {
                    mobileNumberEditText.setError("phone number cannot be empty");
                } else {

                    // Initialize the databaseReference variable
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("students");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isUserVerified = false;
                            for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                                String usn = adminSnapshot.child("usn").getValue(String.class);
                                String phone = adminSnapshot.child("phone").getValue(String.class);
                                Log.d("SignUp", "usn: " + usn);
                                Log.d("SignUp", "phone: " + phone);
                                Log.d("SignUp", "user: " + user);
                                Log.d("SignUp", "p: " + p);

                                if (usn != null && phone != null && usn.trim().equalsIgnoreCase(user) && phone.equalsIgnoreCase(p)) {
                                    isUserVerified = true;
                                    break;
                                }
                            }

                            if (isUserVerified) {
                                Toast.makeText(SignUp.this,"Verified",Toast.LENGTH_SHORT).show();
                                sendOTPButton.setVisibility(View.INVISIBLE);
//                            Toast.makeText(SignUp.this, "User verification done", Toast.LENGTH_SHORT).show();
                                password.setVisibility(View.VISIBLE);
                                confirmPassword.setVisibility(View.VISIBLE);
                                b7.setVisibility(View.VISIBLE);
//                                sendOTP();
                                // Update the databaseReference to "User"

                            } else {
                                Toast.makeText(SignUp.this, "User verification FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("SignUp", "Error retrieving data from database: " + error.getMessage());
                        }
                    });
                }
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input
                String user = usn.getText().toString().trim();
                String pass= password.getText().toString().trim();
                String cp= confirmPassword.getText().toString().trim();
                String phone = mobileNumberEditText.getText().toString().trim();

                // Validate password strength
                if (!isPasswordStrong(pass)) {
                    Toast.makeText(SignUp.this, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show();

                }

//                // Verify password and confirm password match
//                else if (!cp.equals(password)) {
//                    Toast.makeText(SignUp.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
//                }
                else
                {

                    databaseReference.orderByChild("Phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Mobile number exists in the database
                                Toast.makeText(SignUp.this, "Mobile number is already registered", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                HashMap<String, String> usermap = new HashMap<>();
                                usermap.put("USN", user);
                                usermap.put("Phone", phone);
                                usermap.put("password", pass);

                                databaseReference.child(user).setValue(usermap);
                                Toast.makeText(SignUp.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                finish();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("SignUp", "Error updating data in database: " + error.getMessage());
                        }
                    });
                }
            }

        });


    }

//    private void sendOTP() {
//
//
//        mobileNumber = mobileNumberEditText.getText().toString().trim();
//        if (mobileNumber.isEmpty()) {
//            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(otpSent)
//        {
//            final String otp=otpEditText.getText().toString();
//            if(id.isEmpty())
//            {
//                Toast.makeText(SignUp.this,"Unable to Verify",Toast.LENGTH_SHORT).show();
//            }
//            else
//            {
//                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(id,otp);
//                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful())
//                        {
//                            FirebaseUser userDetails=task.getResult().getUser();
//                            Toast.makeText(SignUp.this,"Verified",Toast.LENGTH_SHORT).show();
//                            password.setVisibility(View.VISIBLE);
//                            confirmpassword.setVisibility(View.VISIBLE);
//                            b7.setVisibility(View.VISIBLE);
//
//
//                        }
//                        else
//                        {
//                            Toast.makeText(SignUp.this,"Something went wrong",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }
//        else
//        {
//            final String getmobile=mobileNumberEditText.getText().toString();
//            PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
//                    .setPhoneNumber(countrycode+""+getmobile)
//                    .setTimeout(60L, TimeUnit.SECONDS)
//                    .setActivity(SignUp.this)
//                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                        @Override
//                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                            Toast.makeText(SignUp.this,"Otp sent Successfully",Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onVerificationFailed(@NonNull FirebaseException e) {
//                          Toast.makeText(SignUp.this,"Something went wrong",Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                            super.onCodeSent(s, forceResendingToken);
//                            otpEditText.setVisibility(View.VISIBLE);
//                            sendOTPButton.setText("Verify Otp");
//                            id=s;
//                            otpSent=true;
//                        }
//                    }).build();
//            PhoneAuthProvider.verifyPhoneNumber(options);
//        }
//
//        // Generate a random 6-digit OTP





    private boolean isPasswordStrong(String password) {
        // Check password length
        if (password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*_=+-/].*")) {
            return false;
        }

        return true;
    }

}

//