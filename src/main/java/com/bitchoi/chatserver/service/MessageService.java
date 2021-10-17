package com.bitchoi.chatserver.service;

import com.bitchoi.chatserver.model.Message;
import com.bitchoi.chatserver.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }
}
