package com.utsav.app.models;

/**
 * Represents a pending event request visible on the Manager Dashboard.
 *
 * Firestore path: events/{eventId}
 * Fields written by CreateEventFragment: hostUid, managerId, title, type,
 * date, budgetRange, status, createdAt.
 */
public class EventRequest {

    private String id;
    private String hostUid;
    private String hostName;    // NOT in Firestore — resolved separately if needed
    private String title;
    private String type;
    private String date;
    private String budgetRange;
    private String managerId;
    private String status;
    private long   createdAt;

    public EventRequest() {}    // required by Firestore

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getHostUid()     { return hostUid; }
    public String getHostName()    { return hostName; }
    public String getTitle()       { return title; }
    public String getType()        { return type; }
    public String getDate()        { return date; }
    public String getBudgetRange() { return budgetRange; }
    public String getManagerId()   { return managerId; }
    public String getStatus()      { return status; }
    public long   getCreatedAt()   { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(String id)                   { this.id = id; }
    public void setHostUid(String v)               { this.hostUid = v; }
    public void setHostName(String v)              { this.hostName = v; }
    public void setTitle(String v)                 { this.title = v; }
    public void setType(String v)                  { this.type = v; }
    public void setDate(String v)                  { this.date = v; }
    public void setBudgetRange(String v)           { this.budgetRange = v; }
    public void setManagerId(String v)             { this.managerId = v; }
    public void setStatus(String v)                { this.status = v; }
    public void setCreatedAt(long v)               { this.createdAt = v; }

    // ── Convenience helpers called by EventRequestAdapter ─────────────────────

    /**
     * Compact budget label: "Budget : INR 50000"
     * Strips ₹/commas and takes the lower-bound value before the dash.
     */
    public String getBudgetDisplay() {
        if (budgetRange == null || budgetRange.isEmpty()) return "Budget: —";
        String cleaned = budgetRange
                .replace("₹", "")
                .replace(",", "")
                .trim();
        // "50000 – 100000"  →  take "50000"
        String lower = cleaned.split("[–\\-]")[0].trim();
        return "Budget : INR " + lower;
    }

    /**
     * Single uppercase char for the avatar circle.
     * Prefers hostName, falls back to hostUid first char, then "H".
     */
    public String getInitial() {
        if (hostName != null && !hostName.isEmpty()) {
            return String.valueOf(hostName.charAt(0)).toUpperCase();
        }
        if (hostUid != null && !hostUid.isEmpty()) {
            return String.valueOf(hostUid.charAt(0)).toUpperCase();
        }
        return "H";
    }
}