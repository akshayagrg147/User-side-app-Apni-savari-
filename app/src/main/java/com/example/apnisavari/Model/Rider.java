package com.example.apnisavari.Model;

public class Rider {
    private String email,password,name,phone,profileAks,rates,carType,destination,SwitchingSystem,driverId,driverlat1,driverlng1,vechilenumber;;
    double desLat,desLng;


    public Rider(){


    }

    public Rider(String email, String password, String name, String phone, String profileAks, String rates, String carType, String destination, String switchingSystem, String driverId, String driverlat1, String driverlng1, String vechilenum, double desLat, double desLng) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.profileAks = profileAks;
        this.rates = rates;
        this.carType = carType;
        this.destination = destination;
        SwitchingSystem = switchingSystem;
        this.driverId = driverId;
        this.driverlat1 = driverlat1;
        this.driverlng1 = driverlng1;
        this.vechilenumber = vechilenum;
        this.desLat = desLat;
        this.desLng = desLng;
    }

    public String getVechilenumber() {
        return vechilenumber;
    }

    public void setVechilenumber(String vechilenumber) {
        this.vechilenumber = vechilenumber;
    }

    public String getDriverlat1() {
        return driverlat1;
    }

    public void setDriverlat1(String driverlat1) {
        this.driverlat1 = driverlat1;
    }

    public String getDriverlng1() {
        return driverlng1;
    }

    public void setDriverlng1(String driverlng1) {
        this.driverlng1 = driverlng1;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }



    public String getSwitchingSystem() {
        return SwitchingSystem;
    }

    public void setSwitchingSystem(String switchingSystem) {
        SwitchingSystem = switchingSystem;
    }

    public double getDesLat() {
        return desLat;
    }

    public void setDesLat(double desLat) {
        this.desLat = desLat;
    }

    public double getDesLng() {
        return desLng;
    }

    public void setDesLng(double desLng) {
        this.desLng = desLng;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getProfileAks() {
        return profileAks;
    }

    public void setProfileAks(String profileAks) {
        this.profileAks = profileAks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
