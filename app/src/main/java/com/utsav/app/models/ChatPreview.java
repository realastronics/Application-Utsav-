package com.utsav.app.models;

public class ChatPreview {
    private String chatId;
    private String managerName;
    private String lastMessage;
    private long   timestamp;

    public ChatPreview() {}

    public String getChatId()       { return chatId; }
    public String getManagerName()  { return managerName; }
    public String getLastMessage()  { return lastMessage; }
    public long   getTimestamp()    { return timestamp; }

    public void setChatId(String chatId)            { this.chatId = chatId; }
    public void setManagerName(String managerName)  { this.managerName = managerName; }
    public void setLastMessage(String lastMessage)  { this.lastMessage = lastMessage; }
    public void setTimestamp(long timestamp)        { this.timestamp = timestamp; }
}