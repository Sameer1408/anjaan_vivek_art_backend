package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.model.Painting;
import com.anjaanvivek.artistwebsite.service.PaintingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/paintings")
@Tag(name = "Painting API", description = "APIs for managing paintings")
public class PaintingController {

    @Autowired
    private PaintingService paintingService;

    @Operation(summary = "Add a new painting with multiple images")
    @PostMapping("/add")
    public ResponseEntity<Painting> addPainting(
            @RequestPart("painting") Painting painting,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {

        Painting savedPainting = paintingService.savePainting(painting, images);
        return ResponseEntity.ok(savedPainting);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Painting>> getAllPaintings() {
        List<Painting> paintings = paintingService.getAllPaintings();
        return ResponseEntity.ok(paintings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Painting> getPaintingById(@PathVariable Long id) {
        Painting painting = paintingService.getPaintingById(id);
        if (painting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(painting);
    }
}
