package com.example.Library_Management_System.service.impl;

import com.example.Library_Management_System.exception.SubscriptionPlanException;
import com.example.Library_Management_System.mapper.SubscriptionPlanMapper;
import com.example.Library_Management_System.modal.SubscriptionPlan;
import com.example.Library_Management_System.payload.dto.SubscriptionPlanDTO;
import com.example.Library_Management_System.repository.SubscriptionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionPlanServiceImplTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @InjectMocks
    private SubscriptionPlanServiceImpl subscriptionPlanService;

    private SubscriptionPlan dummyPlan;
    private SubscriptionPlanDTO dummyPlanDTO;

    @BeforeEach
    void setUp() {
        // Setup Entity
        dummyPlan = new SubscriptionPlan();
        dummyPlan.setId(1L);
        dummyPlan.setName("Premium Plan");
        dummyPlan.setPrice(1999L);
        dummyPlan.setDurationDays(1);
        dummyPlan.setMaxBooksAllowed(5);
        dummyPlan.setIsActive(true);

        // Setup DTO
        dummyPlanDTO = new SubscriptionPlanDTO();
        dummyPlanDTO.setId(1L);
        dummyPlanDTO.setName("Premium Plan");
        dummyPlanDTO.setPrice(1999L);
        dummyPlanDTO.setDurationDays(1);
        dummyPlanDTO.setMaxBooksAllowed(5);
        dummyPlanDTO.setIsActive(true);
    }

    // ==================== CREATE ====================

    @Test
    void testCreateSubscriptionPlan_Success() throws SubscriptionPlanException {
        // ARRANGE
        when(subscriptionPlanMapper.toEntity(any(SubscriptionPlanDTO.class))).thenReturn(dummyPlan);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(dummyPlan);
        when(subscriptionPlanMapper.toDTO(any(SubscriptionPlan.class))).thenReturn(dummyPlanDTO);

        // ACT
        SubscriptionPlanDTO result = subscriptionPlanService.createPlan(dummyPlanDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals("Premium Plan", result.getName());
        assertEquals(5, result.getMaxBooksAllowed());
        verify(subscriptionPlanRepository, times(1)).save(dummyPlan);
    }

    // ==================== READ ====================

    @Test
    void testGetSubscriptionPlanById_Success() throws SubscriptionPlanException {
        // ARRANGE
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(dummyPlan));
        when(subscriptionPlanMapper.toDTO(dummyPlan)).thenReturn(dummyPlanDTO);

        // ACT
        SubscriptionPlanDTO result = subscriptionPlanService.getPlanById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(subscriptionPlanRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubscriptionPlanById_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(subscriptionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        SubscriptionPlanException exception = assertThrows(SubscriptionPlanException.class, () -> {
            subscriptionPlanService.getPlanById(99L);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("not found"));
        verify(subscriptionPlanRepository, times(1)).findById(99L);
    }

    // ==================== UPDATE ====================

    @Test
    void testUpdateSubscriptionPlan_Success() throws SubscriptionPlanException {
        // ARRANGE
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(dummyPlan));
        when(subscriptionPlanRepository.save(dummyPlan)).thenReturn(dummyPlan);
        when(subscriptionPlanMapper.toDTO(dummyPlan)).thenReturn(dummyPlanDTO);

        // ACT
        SubscriptionPlanDTO result = subscriptionPlanService.updatePlan(1L, dummyPlanDTO);

        // ASSERT
        assertNotNull(result);
        verify(subscriptionPlanMapper, times(1)).updateEntity(dummyPlan, dummyPlanDTO);

        verify(subscriptionPlanRepository, times(1)).save(dummyPlan);
    }

    @Test
    void testUpdateSubscriptionPlan_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(subscriptionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(SubscriptionPlanException.class, () -> {
            subscriptionPlanService.updatePlan(99L, dummyPlanDTO);
        });

        verify(subscriptionPlanRepository, never()).save(any());
    }

    // ==================== DELETE ====================

    @Test
    void testDeleteSubscriptionPlan_SoftDelete_Success() throws SubscriptionPlanException {
        // ARRANGE
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(dummyPlan));

        // ACT
        subscriptionPlanService.deletePlan(1L);

        // ASSERT
        assertFalse(dummyPlan.getIsActive()); // Verify the active flag was flipped to false
        verify(subscriptionPlanRepository, times(1)).save(dummyPlan); // Verify the update was saved
    }

    @Test
    void testDeleteSubscriptionPlan_WhenNotFound_ShouldThrowException() {
        // ARRANGE
        when(subscriptionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(SubscriptionPlanException.class, () -> {
            subscriptionPlanService.deletePlan(99L);
        });

        verify(subscriptionPlanRepository, never()).save(any());
    }
}