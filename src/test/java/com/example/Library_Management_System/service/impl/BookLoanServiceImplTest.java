package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.domain.BookLoanStatus;
import com.example.Library_Management_System.domain.BookLoanType;
import com.example.Library_Management_System.exception.BookLoanException;
import com.example.Library_Management_System.mapper.BookLoanMapper;
import com.example.Library_Management_System.modal.Book;
import com.example.Library_Management_System.modal.BookLoan;
import com.example.Library_Management_System.modal.User;
import com.example.Library_Management_System.payload.dto.BookLoanDTO;
import com.example.Library_Management_System.payload.dto.SubscriptionDTO;
import com.example.Library_Management_System.payload.request.CheckinRequest;
import com.example.Library_Management_System.payload.request.CheckoutRequest;
import com.example.Library_Management_System.repository.BookLoanRepository;
import com.example.Library_Management_System.repository.BookRepository;
import com.example.Library_Management_System.repository.UserRepository;
import com.example.Library_Management_System.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLoanServiceImplTest {

    @Mock
    private BookLoanRepository bookLoanRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookLoanMapper bookLoanMapper;
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private BookLoanServiceImpl bookLoanService;

    // Static mock for SecurityContextHolder
    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    private User dummyUser;
    private Book dummyBook;
    private BookLoan dummyLoan;
    private SubscriptionDTO dummySubscription;

    @BeforeEach
    void setUp() {
        // Initialize static mock
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);

        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("test@example.com");

        dummyBook = new Book();
        dummyBook.setId(10L);
        dummyBook.setAvailableCopies(5);
        dummyBook.setActive(true);

        dummyLoan = new BookLoan();
        dummyLoan.setId(100L);
        dummyLoan.setUser(dummyUser);
        dummyLoan.setBook(dummyBook);

        dummySubscription = new SubscriptionDTO();
        dummySubscription.setMaxBooksAllowed(5);
        dummySubscription.setMaxDaysPerBook(14);
    }

    @AfterEach
    void tearDown() {
        // CRITICAL: Close the static mock to prevent memory leaks and test cross-contamination
        mockedSecurityContextHolder.close();
    }

    /**
     * Helper method to simulate a logged-in user in the Security Context
     */
    private void mockUserAuthentication(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    // ==================== TEST CASES ====================

    @Test
    void testCheckoutBook_Success() throws Exception {
        // ARRANGE
        mockUserAuthentication("test@example.com");
        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(10L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(dummyUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(subscriptionService.getUsersActiveSubscription(1L)).thenReturn(dummySubscription);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(dummyBook));
        when(bookLoanRepository.hasActiveCheckout(1L, 10L)).thenReturn(false);
        when(bookLoanRepository.countActiveBookLoansByUser(1L)).thenReturn(0L);
        when(bookLoanRepository.countOverdueBookLoansByUser(1L)).thenReturn(0L);
        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(dummyLoan);
        when(bookLoanMapper.toDTO(any())).thenReturn(new BookLoanDTO());

        // ACT
        bookLoanService.checkoutBook(request);

        // ASSERT
        assertEquals(4, dummyBook.getAvailableCopies()); // Inventory decreased
        verify(bookLoanRepository, times(1)).save(any(BookLoan.class));
    }

    @Test
    void testCheckoutBook_WhenLimitReached_ShouldThrowException() throws Exception {
        // ARRANGE
        mockUserAuthentication("test@example.com");
        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(10L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(dummyUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(subscriptionService.getUsersActiveSubscription(1L)).thenReturn(dummySubscription);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(dummyBook));
        
        // Simulate user already having 5 books (max allowed in setup)
        when(bookLoanRepository.countActiveBookLoansByUser(1L)).thenReturn(5L);

        // ACT & ASSERT
        BookLoanException ex = assertThrows(BookLoanException.class, () -> bookLoanService.checkoutBook(request));
        assertTrue(ex.getMessage().contains("subscription limit"));
    }
}