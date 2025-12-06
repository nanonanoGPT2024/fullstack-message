package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import com.example.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message) {
        // Save message to database
        messageRepository.save(message);
        // Broadcast to all subscribers
        simpMessagingTemplate.convertAndSend("/topic/messages", message);
    }
}