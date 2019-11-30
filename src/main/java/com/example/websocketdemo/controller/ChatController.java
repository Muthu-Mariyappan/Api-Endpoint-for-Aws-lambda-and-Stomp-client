package com.example.websocketdemo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
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
		
//		WebSocketClient webSocketClient = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
        
//        Transport wtrans = new WebSocketTransport(new StandardWebSocketClient());
//        List<Transport> list = new ArrayList<Transport>();
//        list.add(wtrans);
//        
//        SockJsClient sjc = new SockJsClient(list);
//        
//        WebSocketStompClient stompClient = new WebSocketStompClient(sjc);
//        stompClient.setMessageConverter(new StringMessageConverter());
//        StompSession session = null;
//        //DefaultStompFrameHandler stompHandler = new DefaultStompFrameHandler();
//        StompSessionHandler sessionsHandler = new MySessionHandler();
//        try {
//            session = stompClient.connect("ws://localhost:5001/ws",sessionsHandler).get(1, TimeUnit.SECONDS);
//            session.subscribe("/topic" + "/channel", sessionsHandler);
//            // do your stuff
//                 
//        } catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//            if (session != null) {
//                session.disconnect();
//            }
//        }
        
        String url = "ws://localhost:5001/ws";
        sessionHandler = new MySessionHandler();
        stompClient.connect(url, sessionHandler);        
        //new Scanner(System.in).nextLine(); //Don't close immediately.
        
	}
	
	@RequestMapping(value = "/base/send")
	public void sendMethod(){
		System.out.println("Base send message called");
		sessionHandler.sendMessage();
	}
	
	@RequestMapping(value = "/base/sendjson")
	public void sendMethodWithJson(){
		System.out.println("Base send message called");
		sessionHandler.sendMessage();
	}
}


