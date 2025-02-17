package com.chat.SunScript.service.Impl;

import com.chat.SunScript.dto.userdto.followdto.CountFollowersResponse;
import com.chat.SunScript.dto.userdto.followdto.CountFollowingResponse;
import com.chat.SunScript.dto.userdto.followdto.FollowResponse;
import com.chat.SunScript.entity.Follow;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.repository.FollowRepository;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.service.FollowService;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Override
    public String followUser(FollowResponse response, Authentication authentication) {
        if (response.getFollowingId() == null || response.getFollowingId().isEmpty()) {
            return "The given id must not be null";
        }
        User follower = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        if (follower == null) {
            return "Follower not found";
        }
        ObjectId objectIdFollowingId;
        try {
            objectIdFollowingId = new ObjectId(response.getFollowingId());
        } catch (IllegalArgumentException e) {
            return "Invalid following user ID format";
        }
        User following = userRepository.findById(objectIdFollowingId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
            return "User is already following this user!";
        }

        Follow follow = new Follow();
        follow.setFollowerId(follower.getId());
        follow.setFollowingId(following.getId());
        follow.setFollowDate(LocalDateTime.now());
        followRepository.save(follow);

        return "User successfully started following!";
    }

    @Override
    public String unfollowUser(FollowResponse response, Authentication authentication) {
        if (response.getFollowingId() == null || response.getFollowingId().isEmpty()) {
            return "The given id must not be null";
        }
        User follower = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        if (follower == null) {
            return "Follower not found";
        }
        //ObjectId followingObjectId = new ObjectId(response.getFollowingId());
        ObjectId objectIdFollowingId;
        try {
            objectIdFollowingId = new ObjectId(response.getFollowingId());
        } catch (IllegalArgumentException e) {
            return "Invalid following user ID format";
        }
        User following = userRepository.findById(objectIdFollowingId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
            return "User is not unfollowing this user!";
        }

        followRepository.deleteByFollowerIdAndFollowingId(follower.getId(), following.getId());

        return "User successfully unfollowing!";
    }

    @Override
    public boolean checkFollow(String userId, Authentication authentication) {
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        ObjectId followingObjectId = new ObjectId(userId);
        return followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), followingObjectId);
    }

    @Override
    public boolean checkForFollowBack(Authentication authentication, String userId) {
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        ObjectId followerObjectId = new ObjectId(userId);
        return followRepository.existsByFollowerIdAndFollowingId(followerObjectId, currentUser.getId());
    }

    @Override
    public CountFollowersResponse getCountFollowers(String id) {
        ObjectId objectId = new ObjectId(id);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = followRepository.countByFollowingId(user.getId());
        return new CountFollowersResponse(count);
    }

    @Override
    public CountFollowingResponse getCountFollowing(String id) {
        ObjectId objectId = new ObjectId(id);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = followRepository.countByFollowerId(user.getId());
        return new CountFollowingResponse(count);
    }


}
