package com.utsav.app.models;

public class ChatMessage {
    private String messageId;
    private String senderId;
    private String senderName;
    private String text;
    private long   timestamp;      // System.currentTimeMillis()
    private boolean sentByUser;    // true = right side, false = left side

    public ChatMessage() {}        // required by Firestore

    public ChatMessage(String senderId, String senderName,
                       String text, boolean sentByUser) {
        this.senderId   = senderId;
        this.senderName = senderName;
        this.text       = text;
        this.sentByUser = sentByUser;
        this.timestamp  = System.currentTimeMillis();
    }

    // Getters
    public String  getMessageId()   { return messageId; }
    public String  getSenderId()    { return senderId; }
    public String  getSenderName()  { return senderName; }
    public String  getText()        { return text; }
    public long    getTimestamp()   { return timestamp; }
    public boolean isSentByUser()   { return sentByUser; }

    // Setters
    public void setMessageId(String messageId)  { this.messageId = messageId; }
    public void setSenderId(String senderId)    { this.senderId = senderId; }
    public void setSenderName(String name)      { this.senderName = name; }
    public void setText(String text)            { this.text = text; }
    public void setTimestamp(long timestamp)    { this.timestamp = timestamp; }
    public void setSentByUser(boolean sentByUser){ this.sentByUser = sentByUser; }
}