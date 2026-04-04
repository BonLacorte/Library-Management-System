package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.exception.GenreException;
import com.example.Library_Management_System.modal.Genre;
import com.example.Library_Management_System.payload.dto.GenreDTO;
import com.example.Library_Management_System.payload.response.ApiResponse;
import com.example.Library_Management_System.service.GenreService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/genres")
public class GenreController {

    private final GenreService genreService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Create a new genre
     * POST /api/genres/create
     */
    @PostMapping("/create")
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genre) {
        try {
            GenreDTO createdGenre = genreService.createGenre(genre);
            return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
        } catch (GenreException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all genres
     * GET /api/genres
     */
    @GetMapping("")
    public ResponseEntity<?> getAllGenres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    /**
     * Get genre by ID
     * GET /api/genres/{genreId}
     */
    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(@PathVariable("genreId") Long genreId) throws GenreException {
        GenreDTO genres = genreService.getGenreById(genreId);
        return ResponseEntity.ok(genres);
    }

    /**
     * Update genre by ID
     * PUT /api/genres/{genreId}
     */
    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(
            @PathVariable("genreId") Long genreId,
            @RequestBody GenreDTO genre
    ) throws GenreException {
        GenreDTO genres = genreService.updateGenre(genreId, genre);
        return ResponseEntity.ok(genres);
    }

    /**
     * Delete genre by ID (Soft Delete)
     * DELETE /api/genres/{genreId}
     */
    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> deleteGenreById(@PathVariable("genreId") Long genreId) throws GenreException {
        genreService.deleteGenre(genreId);
        ApiResponse response = new ApiResponse("Genre Deleted - Soft Delete", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Hard Delete genre by ID
     * DELETE /api/genres/{genreId}/hard
     */
    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<?> hardDeleteGenre(@PathVariable("genreId") Long genreId) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        ApiResponse response = new ApiResponse("Genre Deleted - Hard Delete", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Get top-level genres (Genres with no parent)
     * GET /api/genres/top-level
     */
    @GetMapping("/top-level")
    public ResponseEntity<?> getTopLevelGenres() {
        List<GenreDTO> genres = genreService.getTopLevelGenres();
        return ResponseEntity.ok(genres);
    }

    /**
     * Get total count of active genres
     * GET /api/genres/count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getTotalActivitiesGenres() {
        Long genres = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(genres);
    }

    /**
     * Get book count by genre ID
     * GET /api/genres/{id}/book-count
     */
    @GetMapping("/{id}/book-count")
    public ResponseEntity<?> getBookCountByGenres(@PathVariable Long id) {
        Long count = genreService.getBookCountByGenre(id);
        return ResponseEntity.ok(count);
    }
}
