package com.chat.SunScript.dto.userdto.followdto;

import lombok.Data;

@Data
public class AllFollowingUserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String discriminator;
    private String imageProfile;
    private String imageBackground;
    private long countFollowing;
    private long countFollowers;
    private String username;
    private boolean isOnline;

    public AllFollowingUserResponse() {}

    public AllFollowingUserResponse(String id, String firstName, String lastName, String discriminator, String imageProfile, String imageBackground, long countFollowing, long countFollowers, String username, boolean isOnline) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.discriminator = discriminator;
        this.imageProfile = imageProfile;
        this.imageBackground = imageBackground;
        this.countFollowing = countFollowing;
        this.countFollowers = countFollowers;
        this.username = username;
        this.isOnline = isOnline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageBackground() {
        return imageBackground;
    }

    public void setImageBackground(String imageBackground) {
        this.imageBackground = imageBackground;
    }

    public long getCountFollowing() {
        return countFollowing;
    }

    public void setCountFollowing(long countFollowing) {
        this.countFollowing = countFollowing;
    }

    public long getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(long countFollowers) {
        this.countFollowers = countFollowers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
