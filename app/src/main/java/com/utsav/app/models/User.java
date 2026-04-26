package com.utsav.app.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String role;           // "host" or "manager"
    private String contactNumber;
    private String address;
    private String profileImageUrl;

    public User() {}              // required by Firestore

    public User(String uid, String name, String email, String role) {
        this.uid   = uid;
        this.name  = name;
        this.email = email;
        this.role  = role;
    }

    public String getUid()              { return uid; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getRole()             { return role; }
    public String getContactNumber()    { return contactNumber; }
    public String getAddress()          { return address; }
    public String getProfileImageUrl()  { return profileImageUrl; }

    public void setUid(String uid)                      { this.uid = uid; }
    public void setName(String name)                    { this.name = name; }
    public void setEmail(String email)                  { this.email = email; }
    public void setRole(String role)                    { this.role = role; }
    public void setContactNumber(String contactNumber)  { this.contactNumber = contactNumber; }
    public void setAddress(String address)              { this.address = address; }
    public void setProfileImageUrl(String url)          { this.profileImageUrl = url; }
}