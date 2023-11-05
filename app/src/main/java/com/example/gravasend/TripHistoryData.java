package com.example.gravasend;

public class TripHistoryData {
    private String dateTime;
    private String origin;
    private String destination;
    private String cargo;
    private String weight;
    private String instructions;

    // Default constructor is required for calls to DataSnapshot.getValue(TripHistory.class)
    public TripHistoryData() {
        // Default constructor required for Firebase
    }

    public TripHistoryData(String dateTime, String origin, String destination, String cargo, String weight, String instructions) {
        this.dateTime = dateTime;
        this.origin = origin;
        this.destination = destination;
        this.cargo = cargo;
        this.weight = weight;
        this.instructions = instructions;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getCargo() {
        return cargo;
    }

    public String getWeight() {
        return weight;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
