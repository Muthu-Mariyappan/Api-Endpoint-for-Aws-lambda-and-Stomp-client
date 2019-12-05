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
import com.example.websocketdemo.model.CommandInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
public class ChatController {

	private final String LambdaSenderName;
	private MySessionHandler sessionHandler;
	
	private static final String DRIVER = "org.h2.Driver";
	private static final String URL = "jdbc:h2:~/test";
	private static final String USER = "sa";
	private static final String PASSWORD = "";
	

	public ChatController() {
		this.LambdaSenderName = "Shika";
		
	}
	
	@RequestMapping(value = "/connect")
	public String connectToStompServer(){
		
		String msg = "Failed to establish a sesssion";
		System.out.println("connectToStompServer called ... ");
		
		try {
			
			WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
			List<Transport> transports = new ArrayList<>(1);
			transports.add(new WebSocketTransport(simpleWebSocketClient));
			SockJsClient sockJsClient = new SockJsClient(transports);
			
			WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
			stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
	        
	        //String url = "ws://localhost:5001/ws";
	        String url = "ws://gmm-stomp-broker-in-mem.herokuapp.com/ws";
	        
	        if(sessionHandler==null) {
	        	sessionHandler = new MySessionHandler();
	        	stompClient.connect(url, sessionHandler);
	        	msg = "Connection between you and Stomp server is established.";
	        }else {
	        	msg = "Connection already exists...";
	        }
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        return msg;       
	}
	
	@RequestMapping(value = "/quit")
	public String TerminateSocket(){
		String msg = "Nothing to terminate.";
		if(sessionHandler != null && sessionHandler.isConnected()) {
			sessionHandler.disconnectFromServer();
			sessionHandler = null;
			msg = "Session to Stomp server terminated succesfully.";
		}
		return msg;
	}
	
	
	@RequestMapping(value = "/send")
	public String sendMethod(){
		String msg = "Need to establish session with /connect first.";
		if(sessionHandler != null && sessionHandler.isConnected()) {
			System.out.println("Base send message called");
			sessionHandler.sendMessage();
			msg = "Sample message sent successfully";
		}
		return msg;
	}
	
	@RequestMapping(value = "/sendjson", method = RequestMethod.POST)
	public String sendMethodWithJson(@RequestBody String chatMessage) throws ParseException{
		System.out.println("Base send message called with obj "+chatMessage);
		ChatMessage cms = null;
		CommandInfo cmdInfo = null;
		String sender = null;
		String msg = "Need to establish session with /connect first.";
		if(sessionHandler != null && sessionHandler.isConnected()) {
			
			try {
				cms = fromJsontoChatMessage(chatMessage);
				cmdInfo = fromJsontoCommandInfo(cms.getContent());
				if(cmdInfo!=null) {
					switch(cmdInfo.getCommand()) {
						case "GOD_LOC":
							switch(cmdInfo.getQualifier()) {
								case "CURR":
									ObjectMapper mapper = new ObjectMapper();
									mapper.writeValueAsString(new CommandInfo("GOD_LOC","CURR","10.074951,78.213087"));
									ChatMessage chatMes = new ChatMessage(ChatMessage.MessageType.CHAT,"GMMSERVER",mapper.writeValueAsString(new CommandInfo("GOD_LOC","CURR","10.074951,78.213087")));
									sessionHandler.sendMessage(chatMes);
									break;
								case "PREV":
									break;
								case "NEXT":
									break;
							}
							break;
						case "TANKS":
							switch(cmdInfo.getQualifier()) {
								case "SHOW_ALL":
									break;
								case "SHOW_AT":
									break;
								case "SHOW FILLED":
									break;
								case "SHOW EMPTY":
									break;
							}
							break;
						case "SECURITY":
							switch(cmdInfo.getQualifier()){
								case "COUNT_ALL":
									//showPlace();
									break;
								case "COUNT_AT":
									break;
							}
							break;
						case "PEOPLE":
							switch(cmdInfo.getQualifier()){
								case "COUNT_ALL":
									//countPeople();
									break;
								case "COUNT_AT":
									break;
							}
							break;
						case "CAMERA":
							switch(cmdInfo.getQualifier()){
								case "SHOW":
									//showCamera(cmdData);
									break;
							}
							break;
						case "Bins":
							switch(cmdInfo.getQualifier()){
								case "SHOW_FILLED":
									//showCamera(id);
									break;
							}
							break;
						case "PATH":
							switch(cmdInfo.getQualifier()){
								case "SHOW":
									//showGodPath();
									break;
							}
							break;
						default:
							//console.log("Invalid Command");
						
					}
				}
				sender = cms.getSender();
			} catch (Exception e) {
				e.printStackTrace();
				msg = "Invalid message format.";
			}
			
			System.out.println("Base parsed message ");
			
			try {
				sessionHandler.sendMessage(cms);
				msg = "Message sent. Message is "+cms;
			}catch(Exception e) {
				msg = "Server error. Failed to send message";
			}
			
		}
		return msg;
	}
	
	public ChatMessage fromJsontoChatMessage(String json) throws JsonParseException, JsonMappingException, IOException{
		ChatMessage garima = new ObjectMapper().readValue(json, ChatMessage.class);
		return garima;
	}
	
	public CommandInfo fromJsontoCommandInfo(String json){
		CommandInfo garima = null;
		
		try {
			garima = new ObjectMapper().readValue(json, CommandInfo.class);
		}catch(Exception e) {
			garima = null;
			e.printStackTrace();
		}
		
		return garima;
	}
	
	@Override
	public void finalize() {
		if(sessionHandler!=null) {
			sessionHandler.disconnectFromServer();
		}
	}
	
}


