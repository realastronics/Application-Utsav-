package com.utsav.app.models;

import java.util.List;

public class Event {
    private String id;
    private String hostUid;
    private String managerId;
    private String type;           // "Wedding", "Birthday", etc.
    private String location;
    private String date;           // stored as "dd/MM/yyyy"
    private int    guestCount;
    private String budgetRange;
    private String description;
    private String status;         // "pending", "accepted", "rejected", "completed"
    private String title;
    private String time;
    private List<String> services;
    private String visibility;

    public Event() {}              // required by Firestore

    // Getters
    public String getId()           { return id; }
    public String getHostUid()      { return hostUid; }
    public String getManagerId()    { return managerId; }
    public String getType()         { return type; }
    public String getLocation()     { return location; }
    public String getDate()         { return date; }
    public int    getGuestCount()   { return guestCount; }
    public String getBudgetRange()  { return budgetRange; }
    public String getDescription()  { return description; }
    public String getStatus()       { return status; }
    public String getTitle()             { return title; }
    public String getTime()              { return time; }
    public List<String> getServices()    { return services; }
    public String getVisibility()        { return visibility; }

    // Setters
    public void setId(String id)                { this.id = id; }
    public void setHostUid(String hostUid)      { this.hostUid = hostUid; }
    public void setManagerId(String managerId)  { this.managerId = managerId; }
    public void setType(String type)            { this.type = type; }
    public void setLocation(String location)    { this.location = location; }
    public void setDate(String date)            { this.date = date; }
    public void setGuestCount(int guestCount)   { this.guestCount = guestCount; }
    public void setBudgetRange(String budgetRange){ this.budgetRange = budgetRange; }
    public void setDescription(String description){ this.description = description; }
    public void setStatus(String status)        { this.status = status; }
    public void setTitle(String title)              { this.title = title; }
    public void setTime(String time)               { this.time = time; }
    public void setServices(List<String> services) { this.services = services; }
    public void setVisibility(String visibility)   { this.visibility = visibility; }

}