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
import java.util.Optional;

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
    @Autowired
    private ChatMessageRepository chatMessageRepository;



    @GetMapping("/messages")
    public List<ChatMessage> getAllMessages() {
        return chatService.getAllMessages();
    }


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

    @PutMapping("/messages/{id}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable String id) {
        // Find the message by ID
        Optional<ChatMessage> optionalMessage = chatMessageRepository.findById(id);

        // Check if the message exists
        if (optionalMessage.isPresent()) {
            ChatMessage message = optionalMessage.get();
            message.setRead(true);
            chatMessageRepository.save(message);
            return ResponseEntity.ok().build();
        } else {
            // Return a 404 Not Found status if the message is not found
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/messages/unread")
    public ResponseEntity<List<ChatMessage>> getUnreadMessages(@RequestParam String receiverId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverIdAndRead(receiverId, false);
        return ResponseEntity.ok(unreadMessages);
    }

}