package com.example.Library_Management_System.service;

import com.example.Library_Management_System.exception.GenreException;
import com.example.Library_Management_System.modal.Genre;
import com.example.Library_Management_System.payload.dto.GenreDTO;

import java.util.List;

public interface GenreService {

    // ==================== CRUD OPERATIONS ====================

    /**
     * Create a new genre in the catalog
     * @param genre Genre data
     * @return Created genre DTO
     * @throws GenreException if genre name already exists or validation fails
     */
    GenreDTO createGenre(GenreDTO genreDTO) throws GenreException;

    /**
     * Get all active genres in the catalog
     * @return List of active genre DTOs
     */
    List<GenreDTO> getAllGenres();

    GenreDTO getGenreById(Long id) throws GenreException;

    GenreDTO updateGenre(Long id, GenreDTO genre) throws GenreException;

    void deleteGenre(Long genreId) throws GenreException;

    void hardDeleteGenre(Long genreId) throws GenreException;

    List<GenreDTO> getAllActiveGenresWithSubGenres();

    List<GenreDTO> getTopLevelGenres();

//    Page<GenreDTO> searchGenres(String searchTerm, Pageable pageable);

    long getTotalActiveGenres();

    long getBookCountByGenre(Long genreId);
}
