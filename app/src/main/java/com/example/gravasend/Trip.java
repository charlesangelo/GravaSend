package com.example.gravasend;

public class Trip {
    private String jobOrder;
    private boolean completed;

    public Trip() {
        // Default constructor required for Firebase
    }

    public Trip(String jobOrder, boolean completed) {
        this.jobOrder = jobOrder;
        this.completed = completed;
    }

    public String getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(String jobOrder) {
        this.jobOrder = jobOrder;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
