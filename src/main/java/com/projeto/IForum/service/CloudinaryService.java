package com.projeto.IForum.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dgvvneqnu",
                "api_key", "479283364338553",
                "api_secret", "g1DCM2o-Iy4R5L54g7r8g9JkOUY"));
    }

    public String uploadFile(byte[] fileBytes, String fileName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap("public_id", fileName));
        return (String) uploadResult.get("secure_url");  
    }
}
