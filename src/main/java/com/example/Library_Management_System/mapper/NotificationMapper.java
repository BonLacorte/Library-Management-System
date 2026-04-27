package com.example.Library_Management_System.mapper;

import com.example.Library_Management_System.modal.Notification;
import com.example.Library_Management_System.payload.dto.NotificationDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .deliveryMethod(notification.getDeliveryMethod())
                .readAt(notification.getReadAt())
                .build();
    }
}
