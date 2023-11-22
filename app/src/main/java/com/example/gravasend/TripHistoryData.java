package com.example.gravasend;

import org.checkerframework.checker.guieffect.qual.UI;

public class TripHistoryData {
    private String dateTime;
    private String origin;
    private String destination;
    private String cargo;
    private String weight;
    private String instructions;
    private String driverName;
    private String UID;
    private String status;

    // Default constructor is required for calls to DataSnapshot.getValue(TripHistory.class)
    public TripHistoryData() {
        // Default constructor required for Firebase
    }

    public TripHistoryData(String dateTime, String origin, String destination, String cargo, String weight, String instructions,String driverName,String UID,String status) {
        this.dateTime = dateTime;
        this.origin = origin;
        this.destination = destination;
        this.cargo = cargo;
        this.weight = weight;
        this.instructions = instructions;
        this.driverName = driverName;
        this.UID = UID;
        this.status = status;
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
    public String getDriverName() {
        return driverName;
    }
    public String getUID() {
        return UID;
    }
    public String getStatus() {
        return status;
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
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public void setUID(String UID) {
        this.UID = UID;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
