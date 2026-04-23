package com.utsav.app.models;

public class Manager {
    private String id, name, location, description, eventType, phone;
    private float rating;

    public Manager(String id, String name, String location,
                   float rating, String description,
                   String eventType, String phone) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.description = description;
        this.eventType = eventType;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public float getRating() { return rating; }
    public String getDescription() { return description; }
    public String getEventType() { return eventType; }
    public String getPhone() { return phone; }
}