package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.Painting;
import com.anjaanvivek.artistwebsite.model.PaintingImage;
import com.anjaanvivek.artistwebsite.repository.PaintingRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PaintingService {

    @Autowired
    private PaintingRepository paintingRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Painting savePainting(Painting painting, List<MultipartFile> files) throws IOException {
        
        // If categoryType wasn't provided, default to STUDIO
        if (painting.getCategoryType() == null || painting.getCategoryType().isEmpty()) {
            painting.setCategoryType("STUDIO");
        }

        List<PaintingImage> imageList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("folder", "artist_paintings")
                );

                String imageUrl = uploadResult.get("secure_url").toString();

                PaintingImage img = new PaintingImage();
                img.setImageUrl(imageUrl);
                img.setPainting(painting);

                imageList.add(img);
            }
            painting.setImages(imageList);
        }

        return paintingRepository.save(painting);
    }

    public List<Painting> getAllPaintings() {
        return paintingRepository.findAll();
    }

    public Painting getPaintingById(Long id) {
        return paintingRepository.findById(id).orElse(null);
    }
    
    // You can add a specific method to filter by category if needed later
    // public List<Painting> getPaintingsByCategory(String category) { ... }
}