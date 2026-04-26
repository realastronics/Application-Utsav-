package com.utsav.app.models;

/**
 * A single notification document from Firestore.
 *
 * Firestore path: notifications/{notifId}
 *
 * iconType values: "booking" | "message" | "subscription"
 * type     values: "activity" | "system"
 */
public class NotificationItem {

    private String id;
    private String recipientUid;
    private String type;        // "activity" | "system"
    private String iconType;    // "booking"  | "message" | "subscription"
    private String title;
    private String body;
    private boolean read;
    private long    createdAt;
    private String  deepLink;   // optional — future use

    public NotificationItem() {} // required by Firestore

    // ── Getters ───────────────────────────────────────────────────────────────
    public String  getId()           { return id; }
    public String  getRecipientUid() { return recipientUid; }
    public String  getType()         { return type; }
    public String  getIconType()     { return iconType; }
    public String  getTitle()        { return title; }
    public String  getBody()         { return body; }
    public boolean isRead()          { return read; }
    public long    getCreatedAt()    { return createdAt; }
    public String  getDeepLink()     { return deepLink; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(String id)                   { this.id = id; }
    public void setRecipientUid(String v)          { this.recipientUid = v; }
    public void setType(String v)                  { this.type = v; }
    public void setIconType(String v)              { this.iconType = v; }
    public void setTitle(String v)                 { this.title = v; }
    public void setBody(String v)                  { this.body = v; }
    public void setRead(boolean v)                 { this.read = v; }
    public void setCreatedAt(long v)               { this.createdAt = v; }
    public void setDeepLink(String v)              { this.deepLink = v; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Relative time label: "5m ago", "2h ago", "Yesterday", or date string. */
    public String getRelativeTime() {
        long diff = System.currentTimeMillis() - createdAt;
        long mins  = diff / 60_000;
        long hours = diff / 3_600_000;
        long days  = diff / 86_400_000;

        if (mins  < 1)  return "Just now";
        if (mins  < 60) return mins  + "m ago";
        if (hours < 24) return hours + "h ago";
        if (days  == 1) return "Yesterday";
        return days + "d ago";
    }
}