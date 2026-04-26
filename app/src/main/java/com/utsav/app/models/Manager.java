package com.utsav.app.models;

import java.util.List;

public class Manager {

    private String id;
    private String name;
    private String email;
    private String bio;
    private String location;
    private String phone;
    private String priceRange;
    private String profileImageUrl;
    private float  rating;
    private int    reviewCount;
    private boolean isAvailable;
    private List<String> eventTypes;     // Firestore field: "eventTypes"
    private List<String> portfolioUrls;  // Firestore field: "portfolioUrls"

    public Manager() {}                  // required by Firestore

    // ── Getters ──────────────────────────────────────────────────────────────
    public String  getId()               { return id; }
    public String  getName()             { return name; }
    public String  getEmail()            { return email; }
    public String  getBio()              { return bio; }
    public String  getLocation()         { return location; }
    public String  getPhone()            { return phone; }
    public String  getPriceRange()       { return priceRange; }
    public String  getProfileImageUrl()  { return profileImageUrl; }
    public float   getRating()           { return rating; }
    public int     getReviewCount()      { return reviewCount; }
    public boolean isAvailable()         { return isAvailable; }
    public List<String> getEventTypes()     { return eventTypes; }
    public List<String> getPortfolioUrls()  { return portfolioUrls; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setId(String id)                        { this.id = id; }
    public void setName(String name)                    { this.name = name; }
    public void setEmail(String email)                  { this.email = email; }
    public void setBio(String bio)                      { this.bio = bio; }
    public void setLocation(String location)            { this.location = location; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setPriceRange(String priceRange)        { this.priceRange = priceRange; }
    public void setProfileImageUrl(String url)          { this.profileImageUrl = url; }
    public void setRating(float rating)                 { this.rating = rating; }
    public void setReviewCount(int reviewCount)         { this.reviewCount = reviewCount; }
    public void setAvailable(boolean available)         { this.isAvailable = available; }
    public void setEventTypes(List<String> t)           { this.eventTypes = t; }
    public void setPortfolioUrls(List<String> u)        { this.portfolioUrls = u; }

    // ── Convenience helpers ──────────────────────────────────────────────────

    /** Returns first event type for single-line display in cards. */
    public String getEventType() {
        return (eventTypes != null && !eventTypes.isEmpty())
                ? eventTypes.get(0) : "General";
    }

    /** Used by ManagerAdapter for the description TextView. */
    public String getDescription() { return bio != null ? bio : ""; }
}