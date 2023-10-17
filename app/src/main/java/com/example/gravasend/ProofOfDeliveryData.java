package com.example.gravasend;

public class ProofOfDeliveryData {
    private String eSignature;
    private String date;
    private String time;

    public ProofOfDeliveryData() {
        // Default constructor required for Firebase
    }

    public ProofOfDeliveryData(String eSignature, String date, String time) {
        this.eSignature = eSignature;
        this.date = date;
        this.time = time;
    }

    public String getESignature() {
        return eSignature;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
