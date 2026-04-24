package com.utsav.app.utils;

public class Constants {

    // Firestore collection names
    public static final String COLLECTION_USERS    = "users";
    public static final String COLLECTION_MANAGERS = "managers";
    public static final String COLLECTION_EVENTS   = "events";
    public static final String COLLECTION_CHATS    = "chats";

    // Firestore field names — users
    public static final String FIELD_NAME    = "name";
    public static final String FIELD_EMAIL   = "email";
    public static final String FIELD_ROLE    = "role";

    // Firestore field names — chat
    public static final String FIELD_TIMESTAMP = "timestamp";

    // Intent extra keys
    public static final String EXTRA_MANAGER_ID   = "extra_manager_id";
    public static final String EXTRA_MANAGER_NAME = "extra_manager_name";
    public static final String EXTRA_EVENT_TYPE   = "extra_event_type";
    public static final String EXTRA_CHAT_ID      = "extra_chat_id";

    // User roles
    public static final String ROLE_HOST    = "host";
    public static final String ROLE_MANAGER = "manager";

    // Event status
    public static final String STATUS_PENDING   = "pending";
    public static final String STATUS_ACCEPTED  = "accepted";
    public static final String STATUS_REJECTED  = "rejected";
    public static final String STATUS_COMPLETED = "completed";

    private Constants() {}  // prevent instantiation
}