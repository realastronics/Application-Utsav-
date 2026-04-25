package com.example.utsav.models;
import java.util.List;

public class Manager {
    private String id;
    private String name;
    private String location;
    private String profileImageUrl;
    private List<String> eventTypes;
    private List<String> portfolioImages;

    public Manager(String id, String name, String location,
                   String profileImageUrl,
                   List<String> eventTypes,
                   List<String> portfolioImages) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.profileImageUrl = profileImageUrl;
        this.eventTypes = eventTypes;
        this.portfolioImages = portfolioImages;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public List<String> getPortfolioImages() {
        return portfolioImages;
    }
}