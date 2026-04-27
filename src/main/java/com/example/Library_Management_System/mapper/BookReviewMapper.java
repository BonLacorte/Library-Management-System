package com.example.Library_Management_System.mapper;

import com.example.Library_Management_System.exception.BookReviewException;
import com.example.Library_Management_System.modal.BookReview;
import com.example.Library_Management_System.payload.dto.BookReviewDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for BookReview entity and DTO
 */
@Component
public class BookReviewMapper {

    public BookReviewDTO toDTO(BookReview bookReview) {
        if (bookReview == null) {
            return null;
        }
    
        BookReviewDTO bookReviewDTO = new BookReviewDTO();
        bookReviewDTO.setId(bookReview.getId());
        bookReviewDTO.setUserId(bookReview.getUser().getId());
        bookReviewDTO.setUserName(bookReview.getUser().getFullName());
        bookReviewDTO.setBookId(bookReview.getBook().getId());
        bookReviewDTO.setBookTitle(bookReview.getBook().getTitle());
        bookReviewDTO.setRating(bookReview.getRating());
        bookReviewDTO.setReviewText(bookReview.getReviewText());
        bookReviewDTO.setTitle(bookReview.getTitle());
        bookReviewDTO.setIsVerifiedReader(bookReview.getIsVerifiedReader());
        bookReviewDTO.setIsActive(bookReview.getIsActive());
        bookReviewDTO.setHelpfulCount(bookReview.getHelpfulCount());
        bookReviewDTO.setCreatedAt(bookReview.getCreatedAt());
        bookReviewDTO.setUpdatedAt(bookReview.getUpdatedAt());

        return bookReviewDTO;
    }

    public BookReview toEntity(BookReviewDTO bookReviewDTO) throws BookReviewException {
        if (bookReviewDTO == null) {
            return null;
        }

        BookReview bookReview = new BookReview();
        bookReview.setId(bookReviewDTO.getId());
        bookReview.setRating(bookReviewDTO.getRating());
        bookReview.setReviewText(bookReviewDTO.getReviewText());
        bookReview.setTitle(bookReviewDTO.getTitle());
        bookReview.setIsVerifiedReader(bookReviewDTO.getIsVerifiedReader());
        bookReview.setIsActive(bookReviewDTO.getIsActive());
        bookReview.setHelpfulCount(bookReviewDTO.getHelpfulCount());

        return bookReview;
    }
}
