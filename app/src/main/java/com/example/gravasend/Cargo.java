package com.example.gravasend;

public class Cargo {

    private String cargoType;
    private String cargoWeight;

    public Cargo() {
        // Default constructor required for Firebase
    }

    public Cargo( String cargoType, String cargoWeight) {

        this.cargoType = cargoType;
        this.cargoWeight = cargoWeight;
    }


    public String getCargoType() {
        return cargoType;
    }

    public String getCargoWeight() {
        return cargoWeight;
    }
}
