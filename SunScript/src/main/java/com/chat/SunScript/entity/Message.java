package com.chat.SunScript.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "messages")
public class Message {

    @Id
    private ObjectId id;
    private String content;
    private String sender;
    private String recipient;
    private LocalDateTime timestamp;
    private String status;
    private boolean readStatus;
    private String imageSender;
    private String imageRecipient;
    private MessageType type;
    private List<String> mediaUrls;

    public Message(){}

    public Message(ObjectId id, String content, String sender, String recipient, LocalDateTime timestamp, String status, boolean readStatus, String imageSender, String imageRecipient, MessageType type, List<String> mediaUrls) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = timestamp;
        this.status = status;
        this.readStatus = readStatus;
        this.imageSender = imageSender;
        this.imageRecipient = imageRecipient;
        this.type = type;
        this.mediaUrls = mediaUrls;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getImageSender() {
        return imageSender;
    }

    public void setImageSender(String imageSender) {
        this.imageSender = imageSender;
    }

    public String getImageRecipient() {
        return imageRecipient;
    }

    public void setImageRecipient(String imageRecipient) {
        this.imageRecipient = imageRecipient;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
