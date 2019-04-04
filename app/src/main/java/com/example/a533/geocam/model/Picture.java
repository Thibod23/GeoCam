package com.example.a533.geocam.model;

public class Picture {
    public String name;
    public String lat;
    public String lng;

    public Picture() {
        this.name = "";
        this.lat = "";
        this.lng= "";
    }

    public Picture(String name) {
        this.name = name;
        this.lat = "";
        this.lng= "";
    }

    public Picture(String name, String lat, String lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}
