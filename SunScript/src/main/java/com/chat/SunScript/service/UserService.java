package com.chat.SunScript.service;

import com.chat.SunScript.dto.userdto.GetSearchUserResponse;
import com.chat.SunScript.dto.userdto.GetUserResponse;
import com.chat.SunScript.dto.userdto.LoginUserRequest;
import com.chat.SunScript.dto.userdto.RegisterUserRequest;
import com.chat.SunScript.dto.userdto.followdto.AllFollowingUserResponse;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface UserService {
    GetUserResponse registerUser(RegisterUserRequest request, String imageProfile, String imageBackground);

    GetUserResponse login(LoginUserRequest request);

    List<GetSearchUserResponse> searchUser(String params);
    GetUserResponse getUserByUsername(String username);

    GetUserResponse getOtherUserByDiscrimination(String discrimination);

    List<AllFollowingUserResponse> getAllFollowingUser(Authentication authentication);

}
