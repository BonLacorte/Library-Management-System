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

    @BeforeEach
    void setUp() {
        dummyGenre = Genre.builder()
                .id(1L)
                .code("FIC")
                .name("Fiction")
                .description("Fictional books")
                .displayOrder(1)
                .active(true)
                .build();

        dummyGenreDTO = GenreDTO.builder()
                .id(1L)
                .code("FIC")
                .name("Fiction")
                .description("Fictional books")
                .displayOrder(1)
                .active(true)
                .build();
    }

    // ==================== CREATE ====================

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
        assertEquals("Fiction", result.getName());
        verify(genreRepository, times(1)).save(dummyGenre);
    }

    @Test
    void testCreateGenre_WhenNameAlreadyExists_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.existsByCode("FIC")).thenReturn(true);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.createGenre(dummyGenreDTO);
        });

        assertEquals("Genre name Fiction already exists", exception.getMessage());
        // Verify save was NEVER called because it failed early
        verify(genreRepository, never()).save(any(Genre.class));
    }

    // ==================== READ ====================

    @Test
    void testGetGenreById_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.existsById(1L)).thenReturn(true);
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreMapper.toDTO(dummyGenre)).thenReturn(dummyGenreDTO);

        // ACT
        GenreDTO result = genreService.getGenreById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetGenreById_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.existsById(99L)).thenReturn(false);

        // ACT & ASSERT
        GenreException exception = assertThrows(GenreException.class, () -> {
            genreService.getGenreById(99L);
        });

        assertEquals("genre not found", exception.getMessage());
        verify(genreRepository, never()).findById(anyLong());
    }

    // ==================== UPDATE ====================

    @Test
    void testUpdateGenre_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));
        when(genreRepository.save(dummyGenre)).thenReturn(dummyGenre);
        when(genreMapper.toDTO(dummyGenre)).thenReturn(dummyGenreDTO);

        // ACT
        GenreDTO result = genreService.updateGenre(1L, dummyGenreDTO);

        // ASSERT
        assertNotNull(result);
        // Verify the mapper's update method was called
        verify(genreMapper, times(1)).updateEntityFromDTO(dummyGenreDTO, dummyGenre);
        verify(genreRepository, times(1)).save(dummyGenre);
    }

    @Test
    void testUpdateGenre_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(GenreException.class, () -> {
            genreService.updateGenre(99L, dummyGenreDTO);
        });
        verify(genreRepository, never()).save(any(Genre.class));
    }

    // ==================== DELETE ====================

    @Test
    void testDeleteGenre_SoftDelete_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));

        // ACT
        genreService.deleteGenre(1L);

        // ASSERT
        assertFalse(dummyGenre.getActive());
        verify(genreRepository, times(1)).save(dummyGenre);
    }

    @Test
    void testHardDeleteGenre_Success() throws GenreException {
        // ARRANGE
        when(genreRepository.findById(1L)).thenReturn(Optional.of(dummyGenre));

        // ACT
        genreService.hardDeleteGenre(1L);

        // ASSERT
        verify(genreRepository, times(1)).delete(dummyGenre);
    }

    // ==================== CUSTOM QUERIES ====================

    @Test
    void testGetTopLevelGenres_ShouldReturnList() {
        // ARRANGE
        List<Genre> topLevelGenres = Arrays.asList(dummyGenre);
        when(genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc()).thenReturn(topLevelGenres);
        when(genreMapper.toDTOList(topLevelGenres)).thenReturn(Arrays.asList(dummyGenreDTO));

        // ACT
        List<GenreDTO> result = genreService.getTopLevelGenres();

        // ASSERT
        assertEquals(1, result.size());
        verify(genreRepository, times(1)).findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
    }

    @Test
    void testGetTotalActiveGenres_ShouldReturnCount() {
        // ARRANGE
        when(genreRepository.countByActiveTrue()).thenReturn(5L);

        // ACT
        long count = genreService.getTotalActiveGenres();

        // ASSERT
        assertEquals(5L, count);
        verify(genreRepository, times(1)).countByActiveTrue();
    }
}