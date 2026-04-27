package com.example.Library_Management_System.service;

import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.exception.UserException;
import com.example.Library_Management_System.exception.WishlistException;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.WishlistDTO;
import com.example.Library_Management_System.payload.response.PageResponse;

/**
 * Service interface for wishlist operations
 */
public interface WishlistService {



    /**
     * Add a book to the current user's wishlist
     */
    WishlistDTO addToWishlist(Long bookId, String notes) throws BookException, WishlistException, UserException;

    /**
     * Remove a book from the current user's wishlist
     */
    void removeFromWishlist(Long bookId) throws WishlistException, UserException;

    /**
     * Get all wishlist items for the current authenticated user
     */
    PageResponse<WishlistDTO> getMyWishlist(int page, int size) throws UserException;

    /**
     * Get all wishlist items for a specific user (admin or public view)
     */
    PageResponse<WishlistDTO> getUserWishlist(Long userId, int page, int size);

    /**
     * Check if a book is in the current user's wishlist
     */
    boolean isBookInWishlist(Long bookId) throws UserException;

    /**
     * Update notes for a wishlist item
     */
    WishlistDTO updateWishlistNotes(Long bookId, String notes) throws WishlistException, UserException;

    /**
     * Get total count of wishlist items for current user
     */
    Long getMyWishlistCount() throws UserException;

    /**
     * Get count of how many users have wishlisted a specific book
     */
    Long getBookWishlistCount(Long bookId);
}
