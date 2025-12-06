package com.anjaanvivek.artistwebsite.dto;

import com.anjaanvivek.artistwebsite.model.Painting;

public class WishlistDTO {
    private Painting painting;
    private boolean isInWishlist;

    public WishlistDTO(Painting painting, boolean isInWishlist) {
        this.painting = painting;
        this.isInWishlist = isInWishlist;
    }

    public Painting getPainting() {
        return painting;
    }

    public boolean isInWishlist() {
        return isInWishlist;
    }

    public void setPainting(Painting painting) {
        this.painting = painting;
    }

    public void setInWishlist(boolean inWishlist) {
        isInWishlist = inWishlist;
    }
}