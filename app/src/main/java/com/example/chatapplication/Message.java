package com.example.chatapplication;

public class Message {
    private String message;
    private String senderId;

    // Default constructor
    public Message() {
    }

    // Parameterized constructor
    public Message(String message, String senderId) {
        this.message = message;
        this.senderId = senderId;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter for senderId
    public String getSenderId() {
        return senderId;
    }

    // Setter for senderId
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
