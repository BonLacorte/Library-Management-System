package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.exception.BookException;
import com.example.Library_Management_System.mapper.BookMapper;
import com.example.Library_Management_System.modal.Book;
import com.example.Library_Management_System.payload.dto.BookDTO;
import com.example.Library_Management_System.payload.request.BookSearchRequest;
import com.example.Library_Management_System.payload.response.PageResponse;
import com.example.Library_Management_System.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book dummyBook;
    private BookDTO dummyBookDTO;

    @BeforeEach
    void setUp() {
        dummyBook = new Book();
        dummyBook.setId(1L);
        dummyBook.setIsbn("978-3-16-148410-0");
        dummyBook.setTitle("Spring Boot in Action");
        dummyBook.setTotalCopies(10);
        dummyBook.setAvailableCopies(5);
        dummyBook.setActive(true);

        dummyBookDTO = new BookDTO();
        dummyBookDTO.setId(1L);
        dummyBookDTO.setIsbn("978-3-16-148410-0");
        dummyBookDTO.setTitle("Spring Boot in Action");
        dummyBookDTO.setTotalCopies(10);
        dummyBookDTO.setAvailableCopies(5);
        dummyBookDTO.setGenreId(1L);
    }

    // ==================== CREATE OPERATIONS ====================

    @Test
    void testCreateBook_Success() throws BookException {
        // ARRANGE
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookMapper.toEntity(dummyBookDTO)).thenReturn(dummyBook);
        when(bookRepository.save(dummyBook)).thenReturn(dummyBook);
        when(bookMapper.toDTO(dummyBook)).thenReturn(dummyBookDTO);

        // ACT
        BookDTO result = bookService.createBook(dummyBookDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals("978-3-16-148410-0", result.getIsbn());
        verify(bookRepository, times(1)).save(dummyBook);
    }

    @Test
    void testCreateBook_WhenIsbnExists_ShouldThrowException() {
        // ARRANGE
        when(bookRepository.existsByIsbn(dummyBookDTO.getIsbn())).thenReturn(true);

        // ACT & ASSERT
        BookException exception = assertThrows(BookException.class, () -> {
            bookService.createBook(dummyBookDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testCreateBook_WhenAvailableExceedsTotal_ShouldThrowException() throws BookException {
        // ARRANGE
        dummyBook.setAvailableCopies(15); // Invalid state
        dummyBook.setTotalCopies(10);
        
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookMapper.toEntity(dummyBookDTO)).thenReturn(dummyBook);

        // ACT & ASSERT
        BookException exception = assertThrows(BookException.class, () -> {
            bookService.createBook(dummyBookDTO);
        });

        assertEquals("Available copies cannot exceed total copies", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    // ==================== BULK CREATE OPERATIONS ====================

    @Test
    void testCreateBooksBulk_Success() throws BookException {
        // ARRANGE
        BookDTO secondBookDTO = new BookDTO();
        secondBookDTO.setIsbn("978-1-23-456789-0");
        secondBookDTO.setTotalCopies(5);
        secondBookDTO.setAvailableCopies(5);
        secondBookDTO.setGenreId(2L);

        List<BookDTO> dtoList = Arrays.asList(dummyBookDTO, secondBookDTO);
        
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookMapper.toEntity(any(BookDTO.class))).thenReturn(dummyBook);
        when(bookRepository.saveAll(anyList())).thenReturn(Arrays.asList(dummyBook, dummyBook));
        when(bookMapper.toDTO(any(Book.class))).thenReturn(dummyBookDTO);

        // ACT
        List<BookDTO> result = bookService.createBooksBulk(dtoList);

        // ASSERT
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateBooksBulk_WithDuplicateIsbnInRequest_ShouldThrowException() {
        // ARRANGE - Create two DTOs with the exact same ISBN
        BookDTO duplicateDTO = new BookDTO();
        duplicateDTO.setIsbn("978-3-16-148410-0");
        
        List<BookDTO> dtoList = Arrays.asList(dummyBookDTO, duplicateDTO);

        // ACT & ASSERT
        BookException exception = assertThrows(BookException.class, () -> {
            bookService.createBooksBulk(dtoList);
        });

        assertTrue(exception.getMessage().contains("Duplicate ISBN in request"));
        verify(bookRepository, never()).saveAll(anyList());
    }

    @Test
    void testCreateBooksBulk_WhenEmptyList_ShouldThrowException() {
        // ACT & ASSERT
        assertThrows(BookException.class, () -> {
            bookService.createBooksBulk(new ArrayList<>());
        });
    }

    // ==================== READ OPERATIONS ====================

    @Test
    void testGetBookById_Success() throws BookException {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));
        when(bookMapper.toDTO(dummyBook)).thenReturn(dummyBookDTO);

        // ACT
        BookDTO result = bookService.getBookById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBookById_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(BookException.class, () -> {
            bookService.getBookById(99L);
        });
    }

    @Test
    void testGetBookByIsbn_Success() throws BookException {
        // ARRANGE
        when(bookRepository.findByIsbn("978-3-16-148410-0")).thenReturn(Optional.of(dummyBook));
        when(bookMapper.toDTO(dummyBook)).thenReturn(dummyBookDTO);

        // ACT
        BookDTO result = bookService.getBookByIsbn("978-3-16-148410-0");

        // ASSERT
        assertNotNull(result);
        assertEquals("978-3-16-148410-0", result.getIsbn());
    }

    // ==================== UPDATE OPERATIONS ====================

    @Test
    void testUpdateBook_Success() throws BookException {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));
        when(bookRepository.save(dummyBook)).thenReturn(dummyBook);
        when(bookMapper.toDTO(dummyBook)).thenReturn(dummyBookDTO);

        // ACT
        BookDTO result = bookService.updateBook(1L, dummyBookDTO);

        // ASSERT
        assertNotNull(result);
        verify(bookMapper, times(1)).updateEntityFromDTO(dummyBookDTO, dummyBook);
        verify(bookRepository, times(1)).save(dummyBook);
    }

    @Test
    void testUpdateBook_WhenIsbnChanged_ShouldThrowException() {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));
        
        // Try to update with a different ISBN
        BookDTO updatedDTO = new BookDTO();
        updatedDTO.setIsbn("111-1-11-111111-1");
        updatedDTO.setTotalCopies(10);
        updatedDTO.setAvailableCopies(5);

        // ACT & ASSERT
        BookException exception = assertThrows(BookException.class, () -> {
            bookService.updateBook(1L, updatedDTO);
        });

        assertEquals("ISBN cannot be changed after book creation", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    // ==================== DELETE OPERATIONS ====================

    @Test
    void testDeleteBook_SoftDelete() throws BookException {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));

        // ACT
        bookService.deleteBook(1L);

        // ASSERT
        assertFalse(dummyBook.getActive());
        verify(bookRepository, times(1)).save(dummyBook);
    }

    @Test
    void testHardDeleteBook() throws BookException {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(dummyBook));

        // ACT
        bookService.hardDeleteBook(1L);

        // ASSERT
        verify(bookRepository, times(1)).delete(dummyBook);
    }

    // ==================== SEARCH & STATS ====================

    @Test
    void testSearchBooksWithFilters_ShouldReturnPageResponse() {
        // ARRANGE
        BookSearchRequest request = new BookSearchRequest();
        request.setSearchTerm("Spring");
        request.setGenreId(1L);
        request.setAvailableOnly(true);
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("title");
        request.setSortDirection("ASC");

        Page<Book> dummyPage = new PageImpl<>(Arrays.asList(dummyBook));
        
        // Mock the repository call with generic arguments
        when(bookRepository.searchBooksWithFilters(
                eq("Spring"), 
                eq(1L), 
                eq(true), 
                any(Pageable.class)
        )).thenReturn(dummyPage);

        when(bookMapper.toDTO(dummyBook)).thenReturn(dummyBookDTO);

        // ACT
        PageResponse<BookDTO> response = bookService.searchBooksWithFilters(request);

        // ASSERT
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1L, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
    }

    @Test
    void testGetTotalActiveBooks() {
        // ARRANGE
        when(bookRepository.countByActiveTrue()).thenReturn(150L);

        // ACT
        long result = bookService.getTotalActiveBooks();

        // ASSERT
        assertEquals(150L, result);
    }

    @Test
    void testGetTotalAvailableBooks() {
        // ARRANGE
        when(bookRepository.countAvailableBooks()).thenReturn(100L);

        // ACT
        long result = bookService.getTotalAvailableBooks();

        // ASSERT
        assertEquals(100L, result);
    }
}