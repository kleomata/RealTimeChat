package com.chat.SunScript.controller;

import com.chat.SunScript.dto.userdto.GetSearchUserResponse;
import com.chat.SunScript.dto.userdto.GetUserResponse;
import com.chat.SunScript.dto.userdto.LoginUserRequest;
import com.chat.SunScript.dto.userdto.RegisterUserRequest;
import com.chat.SunScript.dto.userdto.followdto.AllFollowingUserResponse;
import com.chat.SunScript.service.ImageService;
import com.chat.SunScript.service.UserService;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.annotation.Resource;
import org.apache.catalina.Authenticator;
import org.apache.catalina.loader.ResourceEntry;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @PostMapping("/register")
    public ResponseEntity<GetUserResponse> registerUser(
            @Validated
            @ModelAttribute RegisterUserRequest request,
            @RequestParam("imageProfile") MultipartFile imageProfile,
            @RequestParam("imageBackground") MultipartFile imageBackground
    ) {
        String imagePathProfile = imageService.saveImageProfile(imageProfile);
        String imagePathBackground = imageService.saveImageBackground(imageBackground);

        GetUserResponse response = userService.registerUser(request, imagePathProfile, imagePathBackground);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<GetUserResponse> loginUser(@RequestBody LoginUserRequest request) {
        GetUserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }


    //@Secured("ROLE_USER")
    @GetMapping("/search/{params}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<GetSearchUserResponse>> searchUser(
            @Validated @PathVariable String params,
            Authentication authentication
    ) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("Searching for: " + params);
            List<GetSearchUserResponse> searchUserResponses = userService.searchUser(params);
            return ResponseEntity.ok(searchUserResponses);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }

    //////////////////////////////////////////////
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GetUserResponse> getUserById(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            String username  = customUserDetails.getUser().getUsername();

            if (username == null || username.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            GetUserResponse response = userService.getUserByUsername(username);
            if(response == null) {
                return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching customer profile: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/profile/image/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GridFsResource> getProfileImage(@PathVariable("id") String imageProfile) {
        try {
            System.out.println("Retrieving image with ID: " + imageProfile);
            ObjectId objectId = new ObjectId(imageProfile);
            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
            //GridFsResource resource = gridFsTemplate.getResource(objectId);
            if (gridFSFile != null) {
                GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
                if (resource.exists()) {
                    System.out.println("Image found: " + imageProfile);
                    return ResponseEntity.ok()
                            .contentType(getMediaTypeForImage(resource))
                            .body(resource);
                } else {
                    System.out.println("Image not found: " + imageProfile);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } else {
                System.out.println("Image not found: " + imageProfile);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/background/image/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GridFsResource> getBackgroundImage(@PathVariable("id") String imageBackground) {
        try {
            System.out.println("Retrieving image with ID: " + imageBackground);
            ObjectId objectId = new ObjectId(imageBackground);
            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
            if (gridFSFile != null) {
                GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
                if (resource.exists()) {
                    System.out.println("Image found: " + imageBackground);
                    return ResponseEntity.ok()
                            .contentType(getMediaTypeForImage(resource))
                            .body(resource);
                } else {
                    System.out.println("Image not found: " + imageBackground);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } else {
                System.out.println("Image not found: " + imageBackground);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MediaType getMediaTypeForImage(GridFsResource resource) {
        String filename = resource.getFilename().toLowerCase();
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.IMAGE_JPEG;  // Default
    }

    @GetMapping("/discriminator/{discriminator}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GetUserResponse> getUserBYDiscriminator(@PathVariable("discriminator") String discriminator, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            GetUserResponse response = userService.getOtherUserByDiscrimination(discriminator);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }
    @GetMapping("/userInfo/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GetUserResponse> getUserByUsername(@PathVariable("username") String username, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            GetUserResponse response = userService.getUserByUsername(username);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }

    @GetMapping("/allFollowing")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AllFollowingUserResponse>> getAllFollowingForUser(
            @Validated Authentication authentication
    ) {
        List<AllFollowingUserResponse> list = userService.getAllFollowingUser(authentication);
        return ResponseEntity.ok(list);
    }


}
