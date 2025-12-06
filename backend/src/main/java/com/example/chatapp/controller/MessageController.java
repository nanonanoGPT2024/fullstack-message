package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import com.example.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{receiver}")
    public List<Message> getMessages(@PathVariable String receiver) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getReceiver().equals(receiver))
                .collect(Collectors.toList());
    }
}