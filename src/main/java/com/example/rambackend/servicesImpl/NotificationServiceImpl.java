package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Notification;
import com.example.rambackend.repository.NotificationRepository;
import com.example.rambackend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

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

    @Override
    public Notification updateNotification(String id, Notification notification) {
        return notificationRepository.findById(id)

                .map(notification1 -> {
                    if (notification.getDesciption() != null) {
                        notification1.setDesciption(notification.getDesciption());
                    }
                    if (notification.getEtatNotification() != null) {
                        notification1.setEtatNotification(notification.getEtatNotification());
                    }
                    if (notification.getPriorite() != 0) {
                        notification1.setPriorite(notification.getPriorite());
                    }

                    return notificationRepository.save(notification1);
                })
                .orElse(null);
    }

}