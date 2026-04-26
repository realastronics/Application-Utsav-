package com.utsav.app.models;

/**
 * Represents an accepted event shown in the Manager Dashboard Schedule section.
 *
 * Firestore path: events/{eventId}  (status == "accepted")
 */
public class ScheduleItem {

    private String id;
    private String hostUid;
    private String title;
    private String type;
    private String location;
    private String date;
    private String managerId;
    private String status;
    private long   createdAt;

    public ScheduleItem() {}    // required by Firestore

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getId()        { return id; }
    public String getHostUid()   { return hostUid; }
    public String getTitle()     { return title; }
    public String getType()      { return type; }
    public String getLocation()  { return location; }
    public String getDate()      { return date; }
    public String getManagerId() { return managerId; }
    public String getStatus()    { return status; }
    public long   getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(String id)             { this.id = id; }
    public void setHostUid(String hostUid)   { this.hostUid = hostUid; }
    public void setTitle(String title)       { this.title = title; }
    public void setType(String type)         { this.type = type; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date)         { this.date = date; }
    public void setManagerId(String mid)     { this.managerId = mid; }
    public void setStatus(String status)     { this.status = status; }
    public void setCreatedAt(long ts)        { this.createdAt = ts; }

    /** Single-line subtitle: "Location | Date" */
    public String getLocationDateLabel() {
        String loc  = location != null && !location.isEmpty() ? location : "—";
        String d    = date     != null && !date.isEmpty()     ? date     : "—";
        return loc + " | " + d;
    }

    /** Display title with fallback to event type. */
    public String getDisplayTitle() {
        if (title != null && !title.isEmpty()) return title;
        if (type  != null && !type.isEmpty())  return type + " Event";
        return "Untitled Event";
    }
}