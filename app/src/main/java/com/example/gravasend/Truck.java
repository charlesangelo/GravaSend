package com.example.gravasend;

public class Truck {
    private String id;
    private String plateNo;
    private String chassisNo;
    private String engineNo;
    private String model;
    private Long mileage;

    // Default constructor (required for Firebase)
    public Truck() {
    }

    public Truck(String id, String plateNo, String chassisNo, String engineNo, String model, Long mileage) {
        this.id = id;
        this.plateNo = plateNo;
        this.chassisNo = chassisNo;
        this.engineNo = engineNo;
        this.model = model;
        this.mileage = mileage;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public String getModel() {
        return model;
    }

    // Other getters and setters
}
