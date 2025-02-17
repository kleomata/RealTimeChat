package com.chat.SunScript.controller;

import com.chat.SunScript.Util.JwtUtil;
import com.chat.SunScript.dto.userdto.GetUserResponse;
import com.chat.SunScript.dto.userdto.messagedto.MessageDto;
import com.chat.SunScript.entity.Message;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.repository.MessageRepository;
import com.chat.SunScript.service.ChatService;
import com.chat.SunScript.service.ImageService;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RestController
@Controller
//@RequestMapping("/chat")
//@Controller
//@RequestMapping("/api/messages")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/chat/messages/{sender}/{recipient}")
    @PreAuthorize("hasRole('USER')")
    public List<Message> getChatHistory(
            @PathVariable String sender, @PathVariable String recipient
    ) {
        List<Message> senderToRecipient = messageRepository.findBySenderAndRecipient(sender, recipient);
        List<Message> recipientToSender = messageRepository.findBySenderAndRecipient(recipient, sender);

        List<Message> chatHistory = new ArrayList<>();
        chatHistory.addAll(senderToRecipient);
        chatHistory.addAll(recipientToSender);
        chatHistory.sort(Comparator.comparing(Message::getTimestamp));

        return chatHistory;
    }

    @PostMapping("/api/chat/mediaUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<String>> uploadMediaUrls(@RequestParam("mediaUrls")List<MultipartFile> mediaUrls) {
        List<String> upload = imageService.saveMedia(mediaUrls);
        return ResponseEntity.ok(upload);
    }

    @GetMapping("/api/chat/mediaUrls/media/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GridFsResource> getMediaUrls(@PathVariable("id") String mediaUrls) {
        try {
            System.out.println("Retrieving media with ID: " + mediaUrls);
            ObjectId objectId = new ObjectId(mediaUrls);
            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));

            if (gridFSFile != null) {
                GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
                if (resource.exists()) {
                    System.out.println("Media found: "+mediaUrls);
                    return ResponseEntity.ok()
                            .contentType(getMediaTypeForImage(resource))
                            .body(resource);
                } else {
                    System.out.println("Media not found: "+mediaUrls);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } else {
                System.out.println("Media not found: "+mediaUrls);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            System.err.println("Error fetching media: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MediaType getMediaTypeForImage(GridFsResource resource) {
        try {
            String contentType = resource.getContentType();
            if (!contentType.isEmpty()) {
                return MediaType.parseMediaType(contentType);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
   // private boolean isUserOnline(String username) {
     //   return userSessions.contains(username);
    //}


  /*  @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private-message")
    public void sendPrivateMessages(@RequestBody Message message) {
        chatService.saveMessages(message);
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(), "/queue/private", message
        );
    }

    @GetMapping("/{sender}/{recipient}")
    public List<Message> getMessagesBetweenUsers(
            @PathVariable String sender, @PathVariable String recipient
    ) {
        return chatService.getMessagesBetweenUsers(sender,recipient);
    }
*/
   // @GetMapping("/messages/{recipient}")
   // @PreAuthorize("hasRole('USER')")
    /*public ResponseEntity<List<Message>> getCHatHistory(
        @Validated @PathVariable String recipient, Authentication authentication
    ) {
        List<Message> chatHistory = chatService.getChatHistory(authentication, recipient);
        return ResponseEntity.ok(chatHistory);
    }*/

  /* // @MessageMapping("/sendMessages")
    //@PreAuthorize("hasRole('USER')")
    public void sendMessage(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (headerAccessor.getSessionAttributes() == null) {
            headerAccessor.setSessionAttributes(new HashMap<>());
        }
        headerAccessor.getSessionAttributes().put("sender", user.getUsername());
        //String sender = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageDto.setSender(user.getUsername());

        Message saveMessage = chatService.saveMessage(messageDto);

        simpMessagingTemplate.convertAndSendToUser(
                messageDto.getRecipient(),
                "queue/messages",
                saveMessage
        );

    }
*/
}
