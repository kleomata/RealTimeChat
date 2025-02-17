package com.chat.SunScript.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    String saveImageProfile(MultipartFile imageProfile);
    String saveImageBackground(MultipartFile imageBackground);
    List<String> saveMedia(List<MultipartFile> medias);

}
