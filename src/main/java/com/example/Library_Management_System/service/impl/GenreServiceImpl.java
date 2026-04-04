package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.exception.GenreException;
import com.example.Library_Management_System.mapper.GenreMapper;
import com.example.Library_Management_System.modal.Genre;
import com.example.Library_Management_System.payload.dto.GenreDTO;
import com.example.Library_Management_System.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.Library_Management_System.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    // ==================== CRUD OPERATIONS ====================

    @Override
    public GenreDTO createGenre(GenreDTO genreDTO) throws GenreException {
//        return genreRepository.save(genre);
        // Validate genre name
        if (genreRepository.existsByName(genreDTO.getName())) {
            throw new GenreException("Genre name " + genreDTO.getName() + " already exists");
        }

        Genre genre = genreMapper.toEntity(genreDTO);

        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toDTO(savedGenre);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDTO getGenreById(Long genreId) throws GenreException {
        if (!genreRepository.existsById(genreId)) {
            throw new GenreException("genre not found");
        }

        Genre genre =  genreRepository.findById(genreId).orElseThrow(()-> new GenreException("genre not found"));

        return genreMapper.toDTO(genre);
    }

    @Override
    public GenreDTO updateGenre(Long genreId, GenreDTO genreDTO) throws GenreException {
        Genre existingGenre = genreRepository.findById(genreId).orElseThrow(
                () -> new GenreException("Genre not found")
        );

        genreMapper.updateEntityFromDTO(genreDTO, existingGenre);

        Genre updateGenre = genreRepository.save(existingGenre);

        return genreMapper.toDTO(updateGenre);
    }

    @Override
    public void deleteGenre(Long genreId) throws GenreException {
        Genre existingGenre = genreRepository.findById(genreId).orElseThrow(
                () -> new GenreException("Genre not found")
        );

        existingGenre.setActive(false);
        genreRepository.save(existingGenre);
    }

    @Override
    public void hardDeleteGenre(Long genreId) throws GenreException {
        Genre existingGenre = genreRepository.findById(genreId).orElseThrow(
                () -> new GenreException("Genre not found")
        );
        genreRepository.delete(existingGenre);
    }

    @Override
    public List<GenreDTO> getAllActiveGenresWithSubGenres() {

        List<Genre> topLevelGenres = genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public List<GenreDTO> getTopLevelGenres() {

        List<Genre> topLevelGenres = genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public long getTotalActiveGenres() {

        return genreRepository.countByActiveTrue();
    }

    @Override
    public long getBookCountByGenre(Long genreId) {
        return 0;
    }

}
