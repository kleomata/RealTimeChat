package com.chat.SunScript.dto.userdto.followdto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class FollowResponse {

    //private ObjectId followerId;
    private String followingId;

    public FollowResponse() {}

    public FollowResponse(String followingId) {
        this.followingId = followingId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }
}
