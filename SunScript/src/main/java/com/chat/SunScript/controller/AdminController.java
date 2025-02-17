package com.chat.SunScript.controller;

import com.chat.SunScript.dto.admindto.GetAdminResponse;
import com.chat.SunScript.dto.admindto.LoginAdminRequest;
import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody Admin admin) {
        adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body("Admin create successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<GetAdminResponse> login(@RequestBody LoginAdminRequest request) {
        GetAdminResponse adminResponse = adminService.login(request);
        return ResponseEntity.ok(adminResponse);
    }

}
