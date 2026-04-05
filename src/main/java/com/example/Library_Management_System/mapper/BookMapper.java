package com.example.Library_Management_System.mapper;

import com.example.Library_Management_System.modal.Book;
import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.modal.Book;
import com.example.Library_Management_System.modal.Genre;
import com.example.Library_Management_System.payload.dto.BookDTO;
import com.example.Library_Management_System.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Book entity and BookDTO
 */
@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;

    /**
     * Convert Book entity to BookDTO
     */
    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }

        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());

        // Map genre information
        if (book.getGenre() != null) {
            dto.setGenreId(book.getGenre().getId());
            dto.setGenreName(book.getGenre().getName());
        }

        dto.setPublisher(book.getPublisher());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setLanguage(book.getLanguage());
        dto.setPages(book.getPages());
        dto.setDescription(book.getDescription());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setPrice(book.getPrice());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setActive(book.getActive());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());

        return dto;
    }

    /**
     * Convert BookDTO to Book entity
     */
    public Book toEntity(BookDTO dto) throws BookException {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setId(dto.getId());
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());

        // Map genre - fetch from database using genreId
        if (dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new BookException("Genre with ID " + dto.getGenreId() + " not found"));
            book.setGenre(genre);
        }

        book.setPublisher(dto.getPublisher());
        book.setPublicationDate(dto.getPublicationDate());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setPrice(dto.getPrice());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setActive(true); // Default to active


        return book;
    }

    /**
     * Update existing Book entity with data from BookDTO (for update operations)
     */
    public void updateEntityFromDTO(BookDTO dto, Book existingBook) throws BookException {
        if (dto == null || existingBook == null) {
            return;
        }

        // ISBN should not be updated
        existingBook.setTitle(dto.getTitle());
        existingBook.setAuthor(dto.getAuthor());

        // Update genre if provided
        if (dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new BookException("Genre with ID " + dto.getGenreId() + " not found"));
                    existingBook.setGenre(genre);
        }

        existingBook.setPublisher(dto.getPublisher());
        existingBook.setPublicationDate(dto.getPublicationDate());
        existingBook.setLanguage(dto.getLanguage());
        existingBook.setPages(dto.getPages());
        existingBook.setDescription(dto.getDescription());
        existingBook.setTotalCopies(dto.getTotalCopies());
        existingBook.setAvailableCopies(dto.getAvailableCopies());
        existingBook.setPrice(dto.getPrice());
        existingBook.setCoverImageUrl(dto.getCoverImageUrl());

        if (dto.getActive() != null) {
            existingBook.setActive(dto.getActive());
        }
    }
}
