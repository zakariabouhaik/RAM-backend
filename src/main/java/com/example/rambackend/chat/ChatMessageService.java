package com.example.rambackend.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ChatMessageService {


    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Load initial messages if needed
    }

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }
}

