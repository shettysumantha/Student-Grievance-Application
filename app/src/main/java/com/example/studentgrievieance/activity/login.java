package com.example.studentgrievieance.activity;

public class login {

        private String email;
        private String password;

        public login() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public login(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }


