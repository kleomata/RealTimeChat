package com.chat.SunScript.controller;

import com.chat.SunScript.component.WebSocketAuthChannelInterceptor;
import com.chat.SunScript.entity.Message;
import com.chat.SunScript.entity.MessageType;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.entity.UserStatus;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.repository.UserStatusRepository;
import com.chat.SunScript.service.ChatService;
import com.chat.SunScript.service.ImageService;
import com.chat.SunScript.service.WebSocketSessionService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);


    @Autowired
    private WebSocketSessionService webSocketSessionService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @MessageMapping("/connect")
    public void handleConnect(@Payload String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        webSocketSessionService.addSession(username, null);


        UserStatus userStatus = userStatusRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (userStatus == null) {
            userStatus = new UserStatus();
            userStatus.setUsername(username);
        }
        userStatus.setOnline(true);
        userStatus.setLastOnlineTime(LocalDateTime.now());
        userStatusRepository.save(userStatus);

        System.out.println("User: "+username+" -> is now Online at "+ LocalDateTime.now());
        simpMessagingTemplate.convertAndSend("/topic/userStatus", userStatus);
    }

    @MessageMapping("/disconnect")
    public void handleDisconnect(@Payload String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        webSocketSessionService.removeSession(username);
        UserStatus userStatus = userStatusRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        //if (userStatus != null) {
        userStatus.setOnline(false);
        userStatus.setLastOnlineTime(LocalDateTime.now());
        userStatusRepository.save(userStatus);
        System.out.println("User: "+username+" -> is now Offline at"+ LocalDateTime.now());
        //}

        simpMessagingTemplate.convertAndSend("/topic/userStatus", userStatus);
    }

    @MessageMapping("/sendPrivateMessage")
    public void handlePrivateMessages(
            @Payload Message message, Principal principal
    ) {

        String imageSender = getImage(message.getSender());
        String imageRecipient = getImage(message.getRecipient());

        if (principal == null) {
            logger.error("V=Principal is null in handlePrivateMessages!");
        } else {
            logger.info("Messages from {} for {}. Principal: {}",
                    message.getSender(),
                    message.getRecipient(),
                    principal.getName());
        }

        if (message.getMediaUrls() == null) {
            message.setMediaUrls(new ArrayList<>());
        }

        boolean hasText = message.getContent() != null && !message.getContent().trim().isEmpty();
        boolean hasMedia = !message.getMediaUrls().isEmpty();

        List<String> mediaType = new ArrayList<>();
        for (String mediaId: message.getMediaUrls()) {
            GridFSFile mediaFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(mediaId))));
            if (mediaFile != null) {

                String contentType = null;
                if (mediaFile.getMetadata() != null) {
                    contentType = mediaFile.getMetadata().getString("_contentType");
                    if (contentType == null) {
                        contentType = mediaFile.getMetadata().getString("contentType");
                    }
                }

                String filename = mediaFile.getFilename();


                System.out.println("Kontrolli i medias:");
                System.out.println("  - Media ID: " + mediaId);
                System.out.println("  - Filename: " + filename);
                System.out.println("  - Content-Type: " + contentType);



                if (contentType != null) {
                    if (contentType.startsWith("video/")) {
                        mediaType.add("VIDEO");
                    } else if (contentType.startsWith("image/")) {
                        mediaType.add("IMAGE");
                    }
                }


                if (filename != null) {
                    if (filename.endsWith(".mp4") || filename.endsWith(".avi") || filename.endsWith(".mkv")) {
                        mediaType.add("VIDEO");
                    } else if (filename.endsWith(".jpg") ||
                            filename.endsWith(".jpeg") ||
                            filename.endsWith(".png") ||
                            filename.endsWith(".gif") ||
                            filename.endsWith(".bmp") ||
                            filename.endsWith(".tiff") ||
                            filename.endsWith(".webp")) {
                        mediaType.add("IMAGE");
                    }
                }
            }

        }

        boolean hasVideo = mediaType.contains("VIDEO");
        boolean hasImage = mediaType.contains("IMAGE");

        if (hasText && hasMedia) {
            if (hasVideo && hasImage) {
                message.setType(MessageType.MIXED_MEDIA);
            } else {
                message.setType(MessageType.MIXED);
            }
        } else if (hasText) {
            message.setType(MessageType.TEXT);
        } else if (hasMedia) {
            if (hasVideo) {
                message.setType(MessageType.VIDEO);
            } else if (hasImage) {
                message.setType(MessageType.IMAGE);
            }
        } else {
            message.setType(MessageType.TEXT);
        }

        message.setTimestamp(LocalDateTime.now());
        message.setImageSender(imageSender);
        message.setImageRecipient(imageRecipient);
        chatService.saveMessages(message);
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(), "/queue/messages", message);

        System.out.println("Sender: " + message.getSender());
        System.out.println("Recipient: " + message.getRecipient());
        System.out.println("Content: " + message.getContent());
        System.out.println("Image Sender: "+message.getImageSender());
        System.out.println("Image Recipient: "+message.getImageRecipient());
        System.out.println("Type: "+message.getType());
        System.out.println("MediaUrl: "+message.getMediaUrls());
    }

    private String getImage(String username) {
        return userRepository.findByUsername(username)
                .map(User::getImageProfile)
                .orElse("Not found!");
    }


}
