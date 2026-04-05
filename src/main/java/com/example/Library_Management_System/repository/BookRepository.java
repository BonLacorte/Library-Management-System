package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.modal.Book;
import com.example.Library_Management_System.modal.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Book entity.
 * Provides CRUD operations and custom query methods for searching and filtering books.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find a book by ISBN
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if a book exists with the given ISBN
     */
    boolean existsByIsbn(String isbn);


    /**
     * Advanced search with filters - search by title, author, ISBN and filter by genre
     */
    @Query("SELECT b FROM Book b WHERE " +
            // ✅ FIX 2: Check if searchTerm is empty string ('') instead of NULL
            "(:searchTerm = '' OR " +
            "lower(b.title) LIKE lower(CONCAT('%', :searchTerm, '%')) OR " +
            "lower(b.author) LIKE lower(CONCAT('%', :searchTerm, '%')) OR " +
            "lower(b.isbn) LIKE lower(CONCAT('%', :searchTerm, '%'))) AND " +
            
            "(:genreId IS NULL OR b.genre.id = :genreId) AND " +
            "(:availableOnly = false OR b.availableCopies > 0) AND " +
            "b.active = true"
        )
    Page<Book> searchBooksWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("genreId") Long genreId,
            @Param("availableOnly") boolean availableOnly,
            Pageable pageable
    );


    /**
     * Count total active books
     */
    long countByActiveTrue();

    /**
     * Count available books
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.active = true")
    long countAvailableBooks();
}