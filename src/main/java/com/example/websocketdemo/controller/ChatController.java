package com.example.websocketdemo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.websocketdemo.model.ChatMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
public class ChatController {

	private MySessionHandler sessionHandler;
	public ChatController() {
		
	}
	
	@RequestMapping(value = "/base")
	public void testmethod(){
		
		System.out.println("base called");
		
		WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(simpleWebSocketClient));
		SockJsClient sockJsClient = new SockJsClient(transports);
		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
		
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
        
        //String url = "ws://localhost:5001/ws";
        String url = "ws://muthu-stomp-broker-in-mem.herokuapp.com/ws";
        sessionHandler = new MySessionHandler();
        stompClient.connect(url, sessionHandler);        
        //new Scanner(System.in).nextLine(); //Don't close immediately.
        
	}
	
	@RequestMapping(value = "/base/send")
	public void sendMethod(){
		System.out.println("Base send message called");
		sessionHandler.sendMessage();
	}
	
	@RequestMapping(value = "/base/sendjson", method = RequestMethod.POST)
	public void sendMethodWithJson(@RequestBody String chatMessage) throws ParseException{
		System.out.println("Base send message called with obj "+chatMessage);
		ChatMessage cms = null;
		try {
			cms = fromJsontoChatMessage(chatMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Base parsed message ");
		sessionHandler.sendMessage(cms);
	}
	
	public ChatMessage fromJsontoChatMessage(String json) throws JsonParseException, JsonMappingException, IOException{
		ChatMessage garima = new ObjectMapper().readValue(json, ChatMessage.class);
		return garima;
	}
	
}


