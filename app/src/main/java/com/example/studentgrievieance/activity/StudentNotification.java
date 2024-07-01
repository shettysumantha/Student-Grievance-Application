package com.example.studentgrievieance.activity;


public class StudentNotification {
    private String complaintId;
    private String status;
    private String suggestion;
    private String message;

    public StudentNotification() {
    }

    public StudentNotification(String complaintId, String status, String message, String suggestion) {
        this.complaintId = complaintId;
        this.status = status;
        this.suggestion = suggestion;
        this.message= message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }


}
