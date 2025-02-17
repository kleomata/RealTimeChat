package com.chat.SunScript.controller;

import com.chat.SunScript.dto.userStatus.StatusDto;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.entity.UserStatus;
import com.chat.SunScript.repository.UserStatusRepository;
import com.chat.SunScript.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/user")
public class UserStatusController {

    @Autowired
    private UserStatusService  userStatusService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @GetMapping("/status/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> getUserStatus(@PathVariable String username) {

        boolean isOnline = userStatusService.getUserStatus(username);
        return ResponseEntity.ok(isOnline);
    }

    @GetMapping("/userStatus/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StatusDto> getUserStatusForUser(@PathVariable String username) {

        StatusDto statusDto = userStatusService.userStatus(username);
        return ResponseEntity.ok(statusDto);
    }
}
