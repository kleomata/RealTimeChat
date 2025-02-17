package com.chat.SunScript.service.Impl;

import com.chat.SunScript.Util.JwtUtil;
import com.chat.SunScript.dto.userdto.GetSearchUserResponse;
import com.chat.SunScript.dto.userdto.GetUserResponse;
import com.chat.SunScript.dto.userdto.LoginUserRequest;
import com.chat.SunScript.dto.userdto.RegisterUserRequest;
import com.chat.SunScript.dto.userdto.followdto.AllFollowingUserResponse;
import com.chat.SunScript.dto.userdto.followdto.FollowResponse;
import com.chat.SunScript.entity.Follow;
import com.chat.SunScript.entity.Role;
import com.chat.SunScript.entity.User;
import com.chat.SunScript.entity.UserStatus;
import com.chat.SunScript.repository.FollowRepository;
import com.chat.SunScript.repository.UserRepository;
import com.chat.SunScript.repository.UserStatusRepository;
import com.chat.SunScript.service.FollowService;
import com.chat.SunScript.service.UserService;
import com.chat.SunScript.service.custom.CustomUserDetailsService;
import com.chat.SunScript.service.custom.details.CustomUserDetails;
import com.mongodb.client.model.CollationStrength;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DISCRIMINATOR_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();

    @Override
    public GetUserResponse registerUser(RegisterUserRequest request, String imageProfile, String imageBackground) {

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty!");
        }
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(request.getBirthday());
        user.setCountry(request.getCountry());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());

        user.setCreatedDate(LocalDateTime.now());
        user.setImageProfile(imageProfile);
        user.setImageBackground(imageBackground);

        user.setPassword(encryptedPassword);
        user.setRole(Role.ROLE_USER);

        user.setDiscriminator(generateDiscriminator());

        userRepository.save(user);

        UserStatus userStatus = new UserStatus();
        userStatus.setUsername(user.getUsername());
        userStatus.setLastOnlineTime(LocalDateTime.now());
        userStatus.setOnline(false);
        userStatusRepository.save(userStatus);

        return new GetUserResponse(user, imageProfile, imageBackground);
    }

    @Override
    public GetUserResponse login(LoginUserRequest request) {
        try {
            User user = findByUsernameOrEmailWithCollation(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password!");
            }

            String token = jwtUtil.generationToken(user.getUsername());

            return mapUserToGetUserResponse(user, token);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

    private GetUserResponse mapUserToGetUserResponse(User user, String token) {

        GetUserResponse response = new GetUserResponse();

        response.setId(user.getId().toString());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBirthday(user.getBirthday());
        response.setCountry(user.getCountry());
        response.setCity(user.getCity());
        response.setAddress(user.getAddress());
        response.setGender(user.getGender());
        response.setPhone(user.getPhone());
        response.setDiscriminator(user.getDiscriminator());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setBio(user.getBio());

        response.setImageProfile(user.getImageProfile());
        response.setImageBackground(user.getImageBackground());

        response.setToken(token);

        return response;
    }

    @Override
    public List<GetSearchUserResponse> searchUser(String params) {
        List<User> users;

        if (params.contains(" ")) {
            String[] nameParts = params.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts[1];
            users = userRepository.findByFirstNameOrLastNameContainingSeparate(firstName, lastName);
        } else {
            users = userRepository.findByFirstNameOrLastNameOrDiscriminatorContaining(params);
        }

        return users.stream()
                .map(user -> new GetSearchUserResponse(
                        user.getFirstName(), user.getLastName(), user.getDiscriminator(),
                        user.getImageProfile(), user.getImageBackground()
                        )).collect(Collectors.toList());
    }

    @Override
    public GetUserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GetUserResponse response =  new GetUserResponse(user);
        response.setImageProfile(user.getImageProfile());
        response.setImageBackground(user.getImageBackground());

        return response;
    }

    @Override
    public GetUserResponse getOtherUserByDiscrimination(String discrimination) {
        User user = userRepository.findByDiscriminator(discrimination)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GetUserResponse response = new GetUserResponse(user);
        response.setImageBackground(user.getImageBackground());
        response.setImageProfile(user.getImageProfile());
        return response;
    }

    @Override
    public List<AllFollowingUserResponse> getAllFollowingUser(Authentication authentication) {
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        List<Follow> follows = followRepository.findByFollowerId(user.getId());

        return follows.stream()
                .map(follow -> {
                    User userFollow = userRepository.findById(follow.getFollowingId())
                            .orElseThrow(() -> new RuntimeException("User not found"));


                    long countFollowing = followRepository.countByFollowerId(userFollow.getId());
                    long countFollowers = followRepository.countByFollowingId(userFollow.getId());

                    String imageProfile = userFollow.getImageProfile();
                    String imageBackground = userFollow.getImageBackground();

                    UserStatus userStatus = userStatusRepository.findByUsername(userFollow.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found"));


                    boolean isOnline = userStatus.isOnline();
                    System.out.println(isOnline);
                    return new AllFollowingUserResponse(userFollow.getId().toString(), userFollow.getFirstName(), userFollow.getLastName(), userFollow.getDiscriminator(),
                            imageProfile, imageBackground, countFollowing, countFollowers, userFollow.getUsername(), isOnline);
                }).collect(Collectors.toList());

    }

    private String generateDiscriminator() {
        String discriminator;

        do {
            StringBuilder sb = new StringBuilder(DISCRIMINATOR_LENGTH);

            for (int i=0; i < DISCRIMINATOR_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(randomIndex));
            }

            discriminator = sb.toString();

        } while (userRepository.existsByDiscriminator(discriminator));

        return discriminator;
    }

    private Optional<User> findByUsernameOrEmailWithCollation(String usernameOrEmail) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
           Criteria.where("username").is(usernameOrEmail),
           Criteria.where("email").is(usernameOrEmail)
        ));

        query.collation(Collation.of("en"));

        User user = mongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(user);

    }

    private Criteria findByName(String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length == 2) {
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            return new Criteria().orOperator(
                    Criteria.where("firstName").regex(firstName, "i"),
                    Criteria.where("lastName").regex(lastName, "i")
            );
        } else {
            return new Criteria().orOperator(
                    Criteria.where("firstName").regex(name, "i"),
                    Criteria.where("lastName").regex(name, "i")
            );
        }

        //return new Criteria();
    }

    private Criteria findByDiscriminator(String discriminator) {
        return Criteria.where("discriminator").is(discriminator);
    }

}
