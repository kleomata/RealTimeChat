package com.chat.SunScript.dto.userdto.followdto;

import lombok.Data;

@Data
public class CountFollowingResponse {

    private long countFollowing;

    public CountFollowingResponse(){}

    public CountFollowingResponse(long countFollowing) {
        this.countFollowing = countFollowing;
    }

    public long getCountFollowing() {
        return countFollowing;
    }

    public void setCountFollowing(long countFollowing) {
        this.countFollowing = countFollowing;
    }
}
