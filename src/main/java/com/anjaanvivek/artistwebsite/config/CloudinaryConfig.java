package com.anjaanvivek.artistwebsite.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getProperty("cloudinary.cloud_name", "digze3wuc"),
                "api_key", System.getProperty("cloudinary.api_key", "156429342138582"),
                "api_secret", System.getProperty("cloudinary.api_secret", "ZzEcfj4NxcLyXZfj9g7F0IX53Cg")
        ));
    }
}