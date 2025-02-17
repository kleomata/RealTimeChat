package com.chat.SunScript.controller;

import com.chat.SunScript.dto.userdto.followdto.CountFollowersResponse;
import com.chat.SunScript.dto.userdto.followdto.CountFollowingResponse;
import com.chat.SunScript.dto.userdto.followdto.FollowResponse;
import com.chat.SunScript.service.FollowService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/user")
public class FollowController {

    @Autowired
    private FollowService followService;

    // Follow
    @PostMapping("/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> getUserFollow(
            @Validated @RequestBody FollowResponse response, Authentication authentication
    ) {
        try {
            String result = followService.followUser(response, authentication);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message",result);
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Unfollow
    @DeleteMapping("/unfollow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> getUserUnfollow(
            @Validated @RequestBody FollowResponse response, Authentication authentication
    ) {
        try {

            String result = followService.unfollowUser(response, authentication);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", result);
            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }



    // Check
    @GetMapping("/checkFollow/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Boolean>> checkFollow(
            @Validated @PathVariable String userId, Authentication authentication
    ) {
        try {
            boolean isFollowed = followService.checkFollow(userId,authentication);
            Map<String, Boolean> responseBody = new HashMap<>();
            responseBody.put("isFollowed", isFollowed);
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("error", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/checkFollowBack/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Boolean>> checkFollowBack(
            @Validated @PathVariable String userId, Authentication authentication) {
        try {
            boolean isFollowBack = followService.checkForFollowBack(authentication,userId);
            Map<String, Boolean> responseBody = new HashMap<>();
            responseBody.put("isFollowBack", isFollowBack);
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("error", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Count
    @GetMapping("/countFollowers/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CountFollowersResponse> getCountFollowers(
            @Validated @PathVariable String userId, Authentication authentication
    ) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            CountFollowersResponse count = followService.getCountFollowers(userId);
            return ResponseEntity.ok(count);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/countFollowing/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CountFollowingResponse> getCountFollowing(
            @Validated @PathVariable String userId, Authentication authentication
    ) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            CountFollowingResponse count = followService.getCountFollowing(userId);
            return ResponseEntity.ok(count);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }

}
