package com.example.studentgrievieance.activity;
import java.io.Serializable;

public class Complaint implements Serializable {
    private String name;
    private String USN;
    private String category;
    private String Department;
    private String date;
    private String complaintId;
    private String description;
    private String imageId;
    private String status;

    public Complaint() {
        // Default constructor required for Firebase
    }

    public Complaint(String name, String USN, String category, String Department, String date, String description, String imageId, String complaintId,String status) {
        this.name = name;
        this.USN = USN;
        this.category = category;
        this.Department = Department;
        this.date = date;
        this.description = description;
        this.imageId = imageId;
        this.complaintId = complaintId;
        this.status=status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return USN;
    }

    public void setUsn(String USN) {
        this.USN = USN;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
