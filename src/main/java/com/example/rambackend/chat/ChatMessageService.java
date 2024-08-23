package com.example.rambackend.chat;

import com.amazonaws.services.kms.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    ///////////////////////////////////////////////////////////////////////
    public List<ChatMessage> getConversation(String senderId, String receiverId) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                senderId, receiverId, receiverId, senderId);
    }

    public ChatMessage sendMessage(String senderId, String receiverId, String content, ChatMessage.MessageType type) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type); // This should now work
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }



}

