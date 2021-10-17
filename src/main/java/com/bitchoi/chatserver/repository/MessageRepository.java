package com.bitchoi.chatserver.repository;

import com.bitchoi.chatserver.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findAll();
}
