package com.chat.SunScript.dto.userdto.followdto;

import lombok.Data;

@Data
public class CountFollowersResponse {

    private long countFollowers;

    public  CountFollowersResponse() {}


    public CountFollowersResponse(long countFollowers) {
        this.countFollowers = countFollowers;
    }

    public long getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(long countFollowers) {
        this.countFollowers = countFollowers;
    }
}
