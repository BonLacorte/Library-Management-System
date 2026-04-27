package com.example.Library_Management_System.service;

import com.example.Library_Management_System.modal.Notification;
import com.example.Library_Management_System.modal.NotificationSettings;
import com.example.Library_Management_System.modal.User;

/**
 * Service interface for delivering notifications through various channels
 */
public interface NotificationDeliveryService {

    /**
     * Deliver notification through appropriate channels based on settings
     * @param notification The notification to deliver
     * @param settings The user's notification settings
     */
    void deliverNotification(Notification notification, NotificationSettings settings);

    /**
     * Send notification via email
     * @param user The user to notify
     * @param title The notification title
     * @param message The notification message
     */
    void sendEmail(User user, String title, String message);

    /**
     * Send notification via push notification
     * @param user The user to notify
     * @param title The notification title
     * @param message The notification message
     */
    void sendPush(User user, String title, String message);
}
