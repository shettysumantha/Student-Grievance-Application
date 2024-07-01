package com.example.studentgrievieance.activity;
public class FeedbackClass {
    private String id;
    private float rating;
    private String comment;

    public FeedbackClass() {
        // Empty constructor required for Firebase
    }

    public FeedbackClass(String id, float rating, String comment) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
