package com.bitchoi.chatserver.controller;


import com.bitchoi.chatserver.model.Message;
import com.bitchoi.chatserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageRestController {

	@Autowired
	private MessageService messageService;
	
	@GetMapping("/api/messages")
	public List<Message> findAll() {
		return messageService.findAll();
	}
	
}
