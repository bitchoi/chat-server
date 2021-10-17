package com.bitchoi.chatserver.service;

import com.bitchoi.chatserver.model.Message;
import com.bitchoi.chatserver.model.User;
import com.bitchoi.chatserver.repository.MessageRepository;
import com.bitchoi.chatserver.repository.UserRepository;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class SocketIoService {

    private SocketIONamespace namespace;

    private Map<SocketIOClient, String> users = new HashMap<>();

    private SocketIONamespace getNamespace() {
        return namespace;
    }

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public SocketIoService(SocketIOServer server) {
        this.namespace = server.addNamespace("/chat");

        this.namespace.addConnectListener(onConnectListener);
        this.namespace.addDisconnectListener(onDisconnectListener);

        this.namespace.addEventListener("join", User.class, onJoinChat);
        this.namespace.addEventListener("sendMessage", Message.class, onSendMessage);
    }

    public ConnectListener onConnectListener = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
        }
    };

    public DisconnectListener onDisconnectListener = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            namespace.getBroadcastOperations().sendEvent("leave", users.get(client));
            users.remove(client);
            namespace.getBroadcastOperations().sendEvent("count", users.size());
        }
    };

    public DataListener<User> onJoinChat = new DataListener<User>() {
        @Override
        @Transactional
        public void onData(SocketIOClient client, User user, AckRequest ackSender) throws Exception {
            namespace.getBroadcastOperations().sendEvent("newUser", user);
            users.put(client, user.getUsername());
            User newUser;
            var optUser = userRepository.findBySessionId(client.getSessionId().toString());
            if(optUser.isPresent()) {
                newUser = optUser.get();
                newUser.setUsername(user.getUsername());
                newUser.setSessionId(client.getSessionId().toString());
            } else {
                newUser = new User();
                newUser.setUsername(user.getUsername());
                newUser.setSessionId(client.getSessionId().toString());
            }
            userRepository.save(newUser);
            namespace.getBroadcastOperations().sendEvent("count", users.size());
        }
    };

    public DataListener<Message> onSendMessage = new DataListener<Message>() {
        @Override
        @Transactional
        public void onData(SocketIOClient client, Message message, AckRequest arg2) throws Exception {
            namespace.getBroadcastOperations().sendEvent("newMessage", client, message);
            var optUser = userRepository.findBySessionId(client.getSessionId().toString());
            if(optUser.isPresent()) {
                Message newMessage = new Message();
                newMessage.setUser(optUser.get());
                newMessage.setMessage(message.getMessage());
                messageRepository.save(newMessage);
            }
        }
    };
}
