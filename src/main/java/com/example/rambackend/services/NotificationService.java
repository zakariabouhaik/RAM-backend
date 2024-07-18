package com.example.rambackend.services;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    Notification saveNotification(Notification notification);
    List<Notification> getAllNotifications();
    Notification getNotificationById(Long id);
    void deleteNotificationById(Long id);
    Notification updateNotification(Long id, Notification notification);


}
