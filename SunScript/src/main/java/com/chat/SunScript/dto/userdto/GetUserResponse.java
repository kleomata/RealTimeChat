package com.chat.SunScript.dto.userdto;

import com.chat.SunScript.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.UUID;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String country;
    private String city;
    private String address;
    private String gender;
    private String phone;
    private String discriminator;
    private String username;
    private String email;
    //private String password;
    private String bio;
    private String imageProfile;
    private String imageBackground;

    private String token;

    public GetUserResponse() {}

    public GetUserResponse(User user, String imageProfile, String imageBackground) {
        this.id = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthday = user.getBirthday();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.address = user.getAddress();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.discriminator = user.getDiscriminator();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();

        this.imageProfile = imageProfile;
        this.imageBackground = imageBackground;

    }

    /*public GetUserResponse(User user, String token) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthday = user.getBirthday();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.address = user.getAddress();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.discriminator = user.getDiscriminator();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();

        this.imageProfile = user.getImageProfile();
        this.imageBackground = user.getImageBackground();

        this.token = token;

    }*/

    public GetUserResponse(User user) {
        this.id = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthday = user.getBirthday();
        this.country = user.getCountry();
        this.city = user.getCity();
        this.address = user.getAddress();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.discriminator = user.getDiscriminator();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();
        //this.imageProfile = user.getImageProfile();

        //this.imageProfile = user.getImageProfile();
        //this.imageBackground = user.getImageBackground();

       // this.token = token;

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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
