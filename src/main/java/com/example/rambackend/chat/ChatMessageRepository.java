package com.example.rambackend.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            String senderId, String receiverId, String receiverId2, String senderId2);
    List<ChatMessage> findByReceiverIdAndRead(String receiverId, boolean read);


}
