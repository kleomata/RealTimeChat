package com.chat.SunScript.service.Impl;

import com.chat.SunScript.dto.userdto.messagedto.MessageDto;
import com.chat.SunScript.entity.Message;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.repository.MessageRepository;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.service.ChatService;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void saveMessages(Message message) {
        messageRepository.save(message);
    }

    @Override
    public List<Message> getChatHistory(String sender, String recipient) {
        List<Message> senderToRecipient = messageRepository.findBySenderAndRecipient(sender, recipient);
        List<Message> recipientToSender = messageRepository.findBySenderAndRecipient(recipient, sender);

        List<Message> chatHistory = new ArrayList<>();
        chatHistory.addAll(senderToRecipient);
        chatHistory.addAll(recipientToSender);

        chatHistory.sort(Comparator.comparing(Message::getTimestamp));
        return chatHistory;
    }
}
