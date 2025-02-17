package com.chat.SunScript.dto.userStatus;


import java.time.LocalDateTime;

public class StatusDto {

    private boolean isOnline;
    private LocalDateTime lastOnlineTime;

    public StatusDto() {}

    public StatusDto(boolean isOnline, LocalDateTime lastOnlineTime) {
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
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
