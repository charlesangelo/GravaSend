package com.example.gravasend;

public class Cargo {
    private String cargoId;
    private String cargoType;
    private String cargoWeight;

    public Cargo() {
        // Default constructor required for Firebase
    }

    public Cargo(String cargoId, String cargoType, String cargoWeight) {
        this.cargoId = cargoId;
        this.cargoType = cargoType;
        this.cargoWeight = cargoWeight;
    }

    public String getCargoId() {
        return cargoId;
    }

    public String getCargoType() {
        return cargoType;
    }

    public String getCargoWeight() {
        return cargoWeight;
    }
}
