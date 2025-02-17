package com.chat.SunScript.service;

import com.chat.SunScript.dto.admindto.GetAdminResponse;
import com.chat.SunScript.dto.admindto.LoginAdminRequest;
import com.chat.SunScript.entity.Admin;

public interface AdminService {

    void createAdmin(Admin admin);

    GetAdminResponse login(LoginAdminRequest request);

}
