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

    @Override
    public void checkAndCreateNotifications() {
        List<Utilisateur> utilisateurs = userService.getAllUtilisateurs();
        LocalDateTime now = LocalDateTime.now();

        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getRole() == UserRole.AUDITE) {
                LocalDateTime dateCreation = utilisateur.getDateCreation();
                if (dateCreation != null) {
                    long daysSinceCreation = ChronoUnit.DAYS.between(dateCreation, now);

                    if (daysSinceCreation == 30) {
                        Notification notification = new Notification();
                        notification.setDesciption("Il reste 1 mois avant l'expiration du compte de l'audité : " + utilisateur.getNom_complet());
                        notification.setPriorite(1);
                        notification.setEtatNotification(EtatNotification.NONLUE);
                        saveNotification(notification);
                    } else if (daysSinceCreation == 45) {
                        Notification notification = new Notification();
                        notification.setDesciption("Il reste 15 jours avant l'expiration du compte de l'audité : " + utilisateur.getNom_complet());
                        notification.setPriorite(2);
                        notification.setEtatNotification(EtatNotification.NONLUE);
                        saveNotification(notification);
                    }
                }
            }
        }
    }



}