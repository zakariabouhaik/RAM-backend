package com.example.rambackend.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @MessageMapping("/message")
    public void handleMessage(ChatMessage chatMessage) {
        chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/chatroom/public", chatMessage);
    }

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
}