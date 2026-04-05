package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.payload.response.ApiResponse;
import com.example.Library_Management_System.payload.dto.BookDTO;
import com.example.Library_Management_System.payload.request.BookSearchRequest;
import com.example.Library_Management_System.payload.response.PageResponse;
import com.example.Library_Management_System.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Create a new book
     * POST /api/books
     */
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Create multiple books in bulk
     * POST /api/books/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> createBooksBulk(@Valid @RequestBody List<BookDTO> bookDTOs) {
        try {
            List<BookDTO> createdBooks = bookService.createBooksBulk(bookDTOs);
            return new ResponseEntity<>(createdBooks, HttpStatus.CREATED);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * Get a book by ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
//    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) throws BookException, UserException {
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) throws BookException {
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Get a book by ISBN (alternate identifier)
     * GET /api/books/isbn/{isbn}
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) throws BookException {
        BookDTO book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    /**
     * Update a book
     * PUT /api/books/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(updatedBook);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Soft delete a book (mark as inactive)
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id) throws BookException {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book deleted successfully",true));
    }

    /**
     * Permanently delete a book
     * DELETE /api/books/{id}/permanent
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> hardDeleteBook(@PathVariable Long id) throws BookException {
        bookService.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book permanently deleted",true));
    }

    // ==================== UNIFIED SEARCH & LIST ====================

    /**
     * Get/Search books with optional filters via query parameters
     * GET /api/books?genreId=1&availableOnly=true&page=0&size=20
     *
     * Examples:
     * - GET /api/books                                    → All books
     * - GET /api/books?genreId=1                          → Books by genre ID 1
     * - GET /api/books?availableOnly=true                 → Available books
     * - GET /api/books?genreId=1&availableOnly=true       → Available books by genre ID 1
     */
    @GetMapping
    public ResponseEntity<PageResponse<BookDTO>> searchBooks(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        // Build search request from query parameters
        BookSearchRequest searchRequest = new BookSearchRequest();
        searchRequest.setSearchTerm(searchTerm);
        searchRequest.setGenreId(genreId);
        searchRequest.setAvailableOnly(availableOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(books);
    }

    /**
     * Advanced search with multiple filters (complex queries)
     * POST /api/books/search
     *
     * Example request body:
     * {
     *   "searchTerm": "Java Programming",     // Searches title, author, ISBN
     *   "genreId": 1,
     *   "availableOnly": true,
     *   "page": 0,
     *   "size": 20,
     *   "sortBy": "title",
     *   "sortDirection": "ASC"
     * }
     */
    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @RequestBody BookSearchRequest searchRequest) {

        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(books);
    }

    // ==================== STATISTICS ====================

    /**
     * Get book catalog statistics
     * GET /api/books/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats() {
        long totalActive = bookService.getTotalActiveBooks();
        long totalAvailable = bookService.getTotalAvailableBooks();

        BookStatsResponse stats = new BookStatsResponse(totalActive, totalAvailable);
        return ResponseEntity.ok(stats);
    }

    /**
     * Statistics response DTO
     */
    public static class BookStatsResponse {
        public long totalActiveBooks;
        public long totalAvailableBooks;

        public BookStatsResponse(long totalActiveBooks, long totalAvailableBooks) {
            this.totalActiveBooks = totalActiveBooks;
            this.totalAvailableBooks = totalAvailableBooks;
        }
    }
}
