package com.example.rambackend.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
///////////////////////////
@Autowired
private ChatMessageService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private TranslationService translationService;

    /*@MessageMapping("/message")
    public void handleMessage(ChatMessage chatMessage) {
        chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/chatroom/public", chatMessage);
    }*/
    /*@MessageMapping("/message")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType().equals(ChatMessage.MessageType.CHAT)) {
            chatService.saveMessage(chatMessage); // Save message to the database

            if (chatMessage.getReceiver() != null && !chatMessage.getReceiver().isEmpty()) {
                messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "/queue/private", chatMessage);
            } else {
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }*/

    @GetMapping("/messages")
    public List<ChatMessage> getAllMessages() {
        return chatService.getAllMessages();
    }
    /*
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatMessage receiveMessage(@Payload ChatMessage message){
        return message;
    }
*/
    @PostMapping("/translate")
    public ResponseEntity<String> translateMessage(@RequestBody Map<String, Object> request) {
        String message = (String) ((Map<String, Object>) request.get("message")).get("content");

        String translatedText = translationService.translateMessage(message);
        return ResponseEntity.ok(translatedText);
    }
    // Handles private messages sent over WebSocket
    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getReceiverId() == null || chatMessage.getReceiverId().isEmpty()) {
            throw new IllegalArgumentException("Receiver ID must be specified for private messages.");
        }
        chatService.saveMessage(chatMessage);
        String destination = "/user/" + chatMessage.getReceiverId() + "/private";
        messagingTemplate.convertAndSend(destination, chatMessage);
    }
    @MessageMapping("/message")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            chatService.saveMessage(chatMessage);
            if (chatMessage.getReceiverId() != null && !chatMessage.getReceiverId().isEmpty()) {
                messagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(), "/queue/private", chatMessage);
            } else {
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }



}