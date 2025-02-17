package com.chat.SunScript.service.Impl;

import com.chat.SunScript.service.ImageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Override
    public String saveImageProfile(MultipartFile imageProfile) {
        String imagePath = null;

        if (imageProfile != null && !imageProfile.isEmpty()) {
            try {
                ObjectId objectId = gridFsTemplate.store(imageProfile.getInputStream(), imageProfile.getOriginalFilename());
                imagePath = objectId.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imagePath;
    }

    @Override
    public String saveImageBackground(MultipartFile imageBackground) {
        String imagePath = null;

        if (imageBackground != null && !imageBackground.isEmpty()) {
            try {
                ObjectId objectId = gridFsTemplate.store(imageBackground.getInputStream(), imageBackground.getOriginalFilename());
                imagePath = objectId.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imagePath;
    }

    @Override
    public List<String> saveMedia(List<MultipartFile> medias) {
        List<String> mediaUrls = new ArrayList<>();

        if (medias != null && !medias.isEmpty()) {
            for (MultipartFile media: medias) {
                if (!media.isEmpty()) {
                    try {
                        ObjectId objectId = gridFsTemplate.store(
                                media.getInputStream(),
                                media.getOriginalFilename(),
                                media.getContentType());
                        mediaUrls.add(objectId.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mediaUrls;
    }
}
