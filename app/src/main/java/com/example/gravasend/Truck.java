package com.example.gravasend;

public class Truck {
    private String id;
    private String plateNo2;
    private String chassisNo;
    private String engineNo;
    private String model;
    private Long mileage;

    // Default constructor (required for Firebase)
    public Truck() {
    }

    public Truck(String id, String plateNo2, String chassisNo, String engineNo, String model, Long mileage) {
        this.id = id;
        this.plateNo2 = plateNo2;
        this.model = model;
        this.mileage = mileage;
        this.chassisNo = chassisNo;
        this.engineNo = engineNo;
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
    public String getPlateNo() {
        return plateNo2;
    }
    public Long getMileage() {
        return mileage;
    }


    // Other getters and setters
}
