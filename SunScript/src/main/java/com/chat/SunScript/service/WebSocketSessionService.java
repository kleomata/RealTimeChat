package com.chat.SunScript.service;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionService {

    void addSession(String username, WebSocketSession session);
    void removeSession(String username);
    boolean isUserOnline(String username);

}
