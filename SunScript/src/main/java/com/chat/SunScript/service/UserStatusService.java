package com.chat.SunScript.service;

import com.chat.SunScript.dto.userStatus.StatusDto;
import com.chat.SunScript.entity.UserStatus;

public interface UserStatusService {
   boolean getUserStatus(String username);
   StatusDto userStatus(String username);
}
