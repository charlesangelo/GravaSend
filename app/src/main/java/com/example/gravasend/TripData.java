package com.example.gravasend;

public class TripData {
    private String location;
    private String cargoDetails;
    private String specialInstructions;

    public TripData() {
        // Default constructor required for Firebase
    }

    public TripData(String location, String cargoDetails, String specialInstructions) {
        this.location = location;
        this.cargoDetails = cargoDetails;
        this.specialInstructions = specialInstructions;
    }

    public String getCargoDetails() {
        return cargoDetails;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public String getLocation() {
        return location;
    }
}
