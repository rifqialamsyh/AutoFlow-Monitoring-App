package com.example.autoflow.model;

public class DataModel {

    private String id;
    private Integer soil_moisture;
    private Float temperature;
    private Float amount_of_water;
    private String date;

    public DataModel(String id, Integer soil_moisture, Float temperature, Float amount_of_water, String date) {
        this.id = id;
        this.soil_moisture = soil_moisture;
        this.temperature = temperature;
        this.amount_of_water = amount_of_water;
        this.date = date;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public Integer getSoilMoisture() {
        return soil_moisture;
    }

    public void setSoilMoisture(Integer soil_moisture) {
        this.soil_moisture = soil_moisture;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getAmountOfWater() {
        return amount_of_water;
    }

    public void setAmountOfWater(Float amount_of_water) {
        this.amount_of_water = amount_of_water;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}