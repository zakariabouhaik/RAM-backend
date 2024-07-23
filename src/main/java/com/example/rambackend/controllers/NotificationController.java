package com.example.rambackend.controllers;


import com.example.rambackend.entities.Notification;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/Notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    private Notification addNotification(@RequestBody Notification notification){
        return notificationService.saveNotification(notification);
    }

    @GetMapping("/{id}")
    private Notification getNotification(@PathVariable String id){
        return notificationService.getNotificationById(id);
    }

    @GetMapping()
    private List<Notification> getallNotifications(){
        return notificationService.getAllNotifications();
    }

    @PutMapping("/{id}")
    public Notification EditNotification (@PathVariable String id, @RequestBody Notification notification){
        return notificationService.updateNotification(id,notification);
    }


    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable String id){
        notificationService.deleteNotificationById(id);
    }

}


