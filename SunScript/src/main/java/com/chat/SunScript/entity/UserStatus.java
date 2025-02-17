package com.chat.SunScript.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_statuses")
public class UserStatus {
    @Id
    private ObjectId id;
    private String username;
    private boolean isOnline;
    private LocalDateTime lastOnlineTime;

    public UserStatus() {}

    public UserStatus(ObjectId id, String username, boolean isOnline, LocalDateTime lastOnlineTime) {
        this.id = id;
        this.username = username;
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
    }

    public UserStatus(String username, boolean isOnline, LocalDateTime lastOnlineTime) {
        this.username = username;
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public LocalDateTime getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(LocalDateTime lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }
}
