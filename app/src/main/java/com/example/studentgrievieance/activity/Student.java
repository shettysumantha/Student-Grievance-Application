package com.example.studentgrievieance.activity;

import java.io.Serializable;

public class Student implements Serializable {
    private String name;
    private String usn;
    private String phone;
    private String department;
    private String semester;
    private String imageId;

    public Student() {
        // Default constructor required for Firebase Realtime Database
    }

    public Student(String name, String usn, String phone, String department, String semester, String imageId) {
        this.name = name;
        this.usn = usn;
        this.phone = phone;
        this.department = department;
        this.semester = semester;
        this.imageId = imageId;
    }

    // Getters and setters for the properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
