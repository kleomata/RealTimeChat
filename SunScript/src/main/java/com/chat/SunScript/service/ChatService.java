package com.chat.SunScript.service;

import com.chat.SunScript.dto.userdto.messagedto.MessageDto;
import com.chat.SunScript.entity.Message;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {

    void saveMessages(Message message);
    List<Message> getChatHistory(String sender, String recipient);

}
