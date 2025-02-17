package com.chat.SunScript.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "follows")
@Data
public class Follow {

    private ObjectId id;
    private ObjectId followerId;
    private ObjectId followingId;
    private LocalDateTime followDate;

    public Follow() {}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getFollowerId() {
        return followerId;
    }

    public void setFollowerId(ObjectId followerId) {
        this.followerId = followerId;
    }

    public ObjectId getFollowingId() {
        return followingId;
    }

    public void setFollowingId(ObjectId followingId) {
        this.followingId = followingId;
    }

    public LocalDateTime getFollowDate() {
        return followDate;
    }

    public void setFollowDate(LocalDateTime followDate) {
        this.followDate = followDate;
    }
}
