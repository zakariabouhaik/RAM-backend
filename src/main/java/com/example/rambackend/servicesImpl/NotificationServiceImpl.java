package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Notification;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.enums.EtatNotification;
import com.example.rambackend.enums.UserRole;
import com.example.rambackend.repository.NotificationRepository;
import com.example.rambackend.services.NotificationService;
import com.example.rambackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    private UserService userService;

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteNotificationById(String id) {
        notificationRepository.deleteById(id);
    }





}