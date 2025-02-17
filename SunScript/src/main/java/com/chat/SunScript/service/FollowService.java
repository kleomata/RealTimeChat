package com.chat.SunScript.service;

import com.chat.SunScript.dto.userdto.followdto.CountFollowersResponse;
import com.chat.SunScript.dto.userdto.followdto.CountFollowingResponse;
import com.chat.SunScript.dto.userdto.followdto.FollowResponse;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;

public interface FollowService {

    String followUser(FollowResponse response, Authentication authentication);

    String unfollowUser(FollowResponse response, Authentication authentication);

    boolean checkFollow(String userId, Authentication authentication);
    boolean checkForFollowBack(Authentication authentication, String userId);

    CountFollowersResponse getCountFollowers(String id);
    CountFollowingResponse getCountFollowing(String id);


}
