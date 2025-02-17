package com.chat.SunScript.service.Impl;

import com.chat.SunScript.dto.userStatus.StatusDto;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.entity.UserStatus;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.repository.UserStatusRepository;
import com.chat.SunScript.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserStatusImpl implements UserStatusService {

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Override
    public boolean getUserStatus(String username) {
        UserStatus userStatus = userStatusRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userStatus.isOnline();
    }

    @Override
    public StatusDto userStatus(String username) {
        UserStatus userStatus = userStatusRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StatusDto statusDto = new StatusDto();
        statusDto.setOnline(userStatus.isOnline());
        statusDto.setLastOnlineTime(userStatus.getLastOnlineTime());

        System.out.println("UserStatus: "+ statusDto);
        return statusDto;
    }
}
