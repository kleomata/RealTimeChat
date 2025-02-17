package com.chat.SunScript.dto.userdto;

import com.chat.SunScript.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class GetSearchUserResponse {

    private String firstName;
    private String lastName;
    private String discriminator;
    private String imageProfile;
    private String imageBackground;

    public GetSearchUserResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.discriminator = user.getDiscriminator();
        this.imageProfile = user.getImageProfile();
        this.imageBackground = user.getImageBackground();
    }
    public GetSearchUserResponse(){}

    public GetSearchUserResponse(String firstName, String lastName, String discriminator, String imageProfile, String imageBackground) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.discriminator = discriminator;
        this.imageProfile = imageProfile;
        this.imageBackground = imageBackground;
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
}
