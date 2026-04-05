package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.exception.GenreException;
import com.example.Library_Management_System.mapper.GenreMapper;
import com.example.Library_Management_System.modal.Genre;
import com.example.Library_Management_System.payload.dto.GenreDTO;
import com.example.Library_Management_System.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre dummyGenre;
    private GenreDTO dummyGenreDTO;
    private Genre parentGenre;

    @BeforeEach
    void setUp() {
        // Setup Parent Genre
        parentGenre = new Genre();
        parentGenre.setId(100L);
        parentGenre.setCode("ALL-FIC");
        parentGenre.setName("All Fiction");
        parentGenre.setActive(true);

        // Setup Main Dummy Genre
        dummyGenre = new Genre();
        dummyGenre.setId(1L);
        dummyGenre.setCode("FIC");
        dummyGenre.setName("Fiction");
        dummyGenre.setActive(true);
        dummyGenre.setSubGenres(new ArrayList<>()); // Initialize empty list

        // Setup DTO
        dummyGenreDTO = new GenreDTO();
        dummyGenreDTO.setId(1L);
        dummyGenreDTO.setCode("FIC");
        dummyGenreDTO.setName("Fiction");
        dummyGenreDTO.setActive(true);
    }

    // ==================== CREATE OPERATIONS ====================

    @Test
    void testCreateGenre_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.existsByCode("FIC")).thenReturn(false);
        when(genreMapper.toEntity(dummyGenreDTO)).thenReturn(dummyGenre);
        when(genreRepository.save(dummyGenre)).thenReturn(dummyGenre);
        when(genreMapper.toDTO(dummyGenre)).thenReturn(dummyGenreDTO);

        // ACT
        GenreDTO result = genreService.createGenre(dummyGenreDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals("FIC", result.getCode());
        verify(genreRepository, times(1)).save(dummyGenre);
    }

    @Test
    void testCreateGenre_WhenCodeExists_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.existsByCode("FIC")).thenReturn(true);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.createGenre(dummyGenreDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void testCreateGenre_WhenParentIsInactive_ShouldThrowException() {
        // ARRANGE
        dummyGenreDTO.setParentGenreId(100L);
        parentGenre.setActive(false); // Make parent inactive

        when(genreRepository.existsByCode("FIC")).thenReturn(false);
        when(genreRepository.findById(100L)).thenReturn(Optional.of(parentGenre));

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.createGenre(dummyGenreDTO);
        });

        assertEquals("Cannot set an inactive genre as parent", exception.getMessage());
        verify(genreRepository, never()).save(any(Genre.class));
    }

    // ==================== BULK CREATE OPERATIONS ====================

    @Test
    void testCreateGenresBulk_Success() throws GenreException {
        // ARRANGE
        GenreDTO secondGenreDTO = new GenreDTO();
        secondGenreDTO.setCode("SCI-FI");
        
        List<GenreDTO> requestList = Arrays.asList(dummyGenreDTO, secondGenreDTO);

        when(genreRepository.existsByCode(anyString())).thenReturn(false);
        when(genreMapper.toEntity(any(GenreDTO.class))).thenReturn(dummyGenre);
        when(genreRepository.saveAll(anyList())).thenReturn(Arrays.asList(dummyGenre, dummyGenre));
        when(genreMapper.toDTO(eq(dummyGenre), eq(false))).thenReturn(dummyGenreDTO);

        // ACT
        List<GenreDTO> result = genreService.createGenresBulk(requestList);

        // ASSERT
        assertEquals(2, result.size());
        verify(genreRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateGenresBulk_WithDuplicateCodeInRequest_ShouldThrowException() {
        // ARRANGE
        GenreDTO duplicateDTO = new GenreDTO();
        duplicateDTO.setCode("FIC"); // Same code as dummyGenreDTO
        
        List<GenreDTO> requestList = Arrays.asList(dummyGenreDTO, duplicateDTO);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.createGenresBulk(requestList);
        });

        assertTrue(exception.getMessage().contains("Duplicate genre code in request"));
        verify(genreRepository, never()).saveAll(anyList());
    }

    // ==================== UPDATE OPERATIONS ====================

    @Test
    void testUpdateGenre_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreRepository.save(dummyGenre)).thenReturn(dummyGenre);
        when(genreMapper.toDTO(dummyGenre, true)).thenReturn(dummyGenreDTO);

        // ACT
        GenreDTO result = genreService.updateGenre(1L, dummyGenreDTO);

        // ASSERT
        assertNotNull(result);
        verify(genreMapper, times(1)).updateEntityFromDTO(dummyGenreDTO, dummyGenre);
    }

    @Test
    void testUpdateGenre_WhenSettingSelfAsParent_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        dummyGenreDTO.setParentGenreId(1L); // Trying to make it its own parent

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.updateGenre(1L, dummyGenreDTO);
        });

        assertEquals("Genre cannot be its own parent", exception.getMessage());
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void testUpdateGenre_WhenCircularReferenceDetected_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        
        // Setup the loop: We are updating Genre 1. We want to set its parent to Genre 100.
        dummyGenreDTO.setParentGenreId(100L); 
        
        // BUT, Genre 100 currently has Genre 1 as ITS parent. (Circular Loop)
        parentGenre.setParentGenre(dummyGenre); 
        
        when(genreRepository.findById(100L)).thenReturn(Optional.of(parentGenre));

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.updateGenre(1L, dummyGenreDTO);
        });

        assertTrue(exception.getMessage().contains("Circular reference detected"));
        verify(genreRepository, never()).save(any(Genre.class));
    }

    // ==================== DELETE OPERATIONS ====================

    @Test
    void testHardDeleteGenre_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreRepository.isGenreInUse(1L)).thenReturn(false);

        // ACT
        genreService.hardDeleteGenre(1L);

        // ASSERT
        verify(genreRepository, times(1)).delete(dummyGenre);
    }

    @Test
    void testHardDeleteGenre_WhenInUse_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreRepository.isGenreInUse(1L)).thenReturn(true);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.hardDeleteGenre(1L);
        });

        assertTrue(exception.getMessage().contains("currently assigned to one or more books"));
        verify(genreRepository, never()).delete(any(Genre.class));
    }

    @Test
    void testHardDeleteGenre_WhenHasSubGenres_ShouldThrowException() {
        // ARRANGE
        dummyGenre.getSubGenres().add(new Genre()); // Add a sub-genre to the list
        
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreRepository.isGenreInUse(1L)).thenReturn(false);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.hardDeleteGenre(1L);
        });

        assertTrue(exception.getMessage().contains("has sub-genres"));
        verify(genreRepository, never()).delete(any(Genre.class));
    }
}