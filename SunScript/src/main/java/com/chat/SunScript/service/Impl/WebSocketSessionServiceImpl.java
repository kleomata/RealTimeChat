package com.chat.SunScript.service.Impl;

import com.chat.SunScript.service.WebSocketSessionService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketSessionServiceImpl implements WebSocketSessionService {

    private Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void addSession(String username, WebSocketSession session) {
        sessions.put(username, session);
    }

    @Override
    public void removeSession(String username) {
        WebSocketSession session = sessions.get(username);
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sessions.remove(username);
            }
        }
    }

    @Override
    public boolean isUserOnline(String username) {
        return sessions.containsKey(username);
    }
}
