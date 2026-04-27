package com.example.Library_Management_System.mapper;

import com.example.Library_Management_System.modal.Wishlist;
import com.example.Library_Management_System.payload.dto.WishlistDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Wishlist entity and WishlistDTO
 */
@Component
@RequiredArgsConstructor
public class WishlistMapper {

    private final BookMapper bookMapper;

    /**
     * Convert Wishlist entity to WishlistDTO
     */
    public WishlistDTO toDTO(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());

        if (wishlist.getUser() != null) {
            dto.setUserId(wishlist.getUser().getId());
            dto.setUserFullName(wishlist.getUser().getFullName());
        }

        if (wishlist.getBook() != null) {
            dto.setBook(bookMapper.toDTO(wishlist.getBook()));
        }

        dto.setAddedAt(wishlist.getAddedAt());
        dto.setNotes(wishlist.getNotes());

        return dto;
    }
}

