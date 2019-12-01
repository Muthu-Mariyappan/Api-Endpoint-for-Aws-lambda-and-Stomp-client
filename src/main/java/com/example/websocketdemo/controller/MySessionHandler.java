package com.example.websocketdemo.controller;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.example.websocketdemo.model.ChatMessage;

import java.lang.reflect.Type;
import java.util.Scanner;

public class MySessionHandler extends StompSessionHandlerAdapter {
	
	private StompSession cSession;
	
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    	System.out.println("Afterconnected called");
    	
    	cSession = session;
    	
    	session.subscribe("/topic/public", this);
        session.send("/app/chat.addUser", new ChatMessage(ChatMessage.MessageType.JOIN,"muthu","none"));
        
        System.out.println("Joined the session. Make call to /base/send");
        
        System.out.println("New session: "+ session.getSessionId());
        //session.send("/app/chat.sendMessage", "{\"sender\":\"Muthu\",\"type\":\"CHAT\",\"content\":\"Message from muthu\"}");
        //session.send("/app/chat.sendMessage", new ChatMessage(ChatMessage.MessageType.CHAT,"muthu","Message"));
        System.out.println("Afterconnected finished.");
    }

    public boolean isConnected() {
    	return cSession != null;
    }
    
    public StompSession getSession() {
    	return this.cSession;
    }
    
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }
        
    public void sendMessage() {
    	System.out.println("Session handler send message");
    	cSession.send("/app/chat.sendMessage", new ChatMessage(ChatMessage.MessageType.CHAT,"muthu","Message"));    	
    }
    
    public void disconnectFromServer() {
    	System.out.println("Disconnect called...");
    	cSession.disconnect(); 	
    }
    
    public void sendMessage(ChatMessage chatMessage)throws Exception {
    	System.out.println("Message received from client : "+chatMessage);
    	cSession.send("/app/chat.sendMessage", chatMessage);   	
    }
    
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Received: "+ ((ChatMessage) payload).getContent());
    }
}