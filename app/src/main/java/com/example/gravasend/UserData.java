package com.example.gravasend;

public class UserData {
    private String plateNumber;
    private String odometerReading;
    private String imageDescription;
    private boolean isChecked;
    private String imageUrl;
    public UserData() {
        // Default constructor required for Firebase
    }

    public UserData(String plateNumber, String odometerReading, String imageDescription, boolean isChecked) {
        this.plateNumber = plateNumber;
        this.odometerReading = odometerReading;
        this.imageDescription = imageDescription;
        this.isChecked = isChecked;
        this.imageUrl = imageUrl;
    }

    public UserData(String plateNumber, String odometerReading, String imageDescription, boolean isChecked, String imageUrl) {
    }

    // Add getters and setters
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(String odometerReading) {
        this.odometerReading = odometerReading;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageUrl() {
        return imageUrl; // Return the image URL
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl; // Set the image URL
    }
}
