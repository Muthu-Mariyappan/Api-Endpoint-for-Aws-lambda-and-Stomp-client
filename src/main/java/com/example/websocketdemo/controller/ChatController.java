package com.example.websocketdemo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.CommandInfo;
import com.example.websocketdemo.model.MyConstants;
import com.example.websocketdemo.model.Place;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
public class ChatController {

	private MySessionHandler sessionHandler;
	private HttpHelper httpHelper = new HttpHelper();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public ChatController() {
		
	}
	
	private ScheduledFuture<?> scheduledFuture[] = new ScheduledFuture<?>[3];
	private ScheduledExecutorService ses[] = new ScheduledExecutorService[3];
	
	@RequestMapping(value = "/schedule/start")
	public String scheduleCall() throws InterruptedException {
		
		ses[0] = Executors.newScheduledThreadPool(2);
		//ses[1] = Executors.newScheduledThreadPool(2);
		//ses[2] = Executors.newScheduledThreadPool(2);
		
        Runnable God = () -> {
        	String result = null;
			try {
				result = httpHelper.sendGet(MyConstants.SimBaseURL+MyConstants.getassetsbytypeid.replace("tyid", "4"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
        	CommandInfo commandInfo = new CommandInfo("GOD_LOC","CURR",result);
        	ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.CHAT,MyConstants.StompServer,convertToJSON(commandInfo));
        	
        	//ChatMessage bigMess = new ChatMessage(ChatMessage.MessageType.CHAT,MyConstants.StompServer,"biggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggMessssssssssssssssssssssssssssssssssssssssssssssssssssssssageeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        	sessionHandler.sendMessage(chatMessage);
        };
        
        Runnable Security = () -> {
        	String result = null;
			try {
				//result = httpHelper.sendGet("https://say-hello-gmm.herokuapp.com/sayhello?name=muthu2");
				result = httpHelper.sendGet(MyConstants.SimBaseURL+MyConstants.getassetsbyvaluegreater.replace("type", "3").replace("rvalue", "90"));

			} catch (Exception e) {
				e.printStackTrace();
			}
			
        	CommandInfo commandInfo = new CommandInfo("ALLDATA","NONE",result);
        	ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.CHAT,MyConstants.StompServer,convertToJSON(commandInfo));
        	
        	sessionHandler.sendMessage(chatMessage);
        	
        };
        
        Runnable Ambulance = () -> {
        	String result = null;
			try {
				//result = httpHelper.sendGet("https://say-hello-gmm.herokuapp.com/sayhello?name=muthu3");
				result = httpHelper.sendGet(MyConstants.SimBaseURL+MyConstants.getassetsbyvaluesmaller.replace("type", "1").replace("rvalue", "20"));

			} catch (Exception e) {
				e.printStackTrace();
			}
        	CommandInfo commandInfo = new CommandInfo("ALLDATA","NONE",result);
        	ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.CHAT,MyConstants.StompServer,convertToJSON(commandInfo));
        	sessionHandler.sendMessage(chatMessage);
        };
        
        scheduledFuture[0] = ses[0].scheduleAtFixedRate(God, 0, 5, TimeUnit.SECONDS);
        //scheduledFuture[1] = ses[1].scheduleAtFixedRate(Security, 3, 5, TimeUnit.SECONDS);
        //scheduledFuture[2] = ses[2].scheduleAtFixedRate(Ambulance, 6, 5, TimeUnit.SECONDS);
        return "Streaming locations....";
	}
	
	private String convertToJSON(Object object) {
		String resultJson = null;
		
		try {
			resultJson = objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			System.out.println("Error converting to json string");
			e.printStackTrace();
		}
		return resultJson;
	}
	
	@RequestMapping(value = "/schedule/cancel")
	public String cancelScheduleCall() throws InterruptedException {
		scheduledFuture[0].cancel(true);
		//scheduledFuture[1].cancel(true);
		//scheduledFuture[2].cancel(true);
        ses[0].shutdown();
        //ses[1].shutdown();
        //ses[2].shutdown();
        return "Locations stream stopped..";
	}
	
	@RequestMapping(value = "/server/get")
	public void getToServer() throws Exception {
		httpHelper.sendGet();
	}
	
	@RequestMapping(value = "/server/post")
	public void postToServer() throws Exception {
		httpHelper.sendPost();
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
	        
	        String url = MyConstants.BrokerURL;
	        //String url = "ws://gmm-stomp-broker-in-mem.herokuapp.com/ws";
	        
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
	
	public String ripJsonArray(String json) {
		return json.replace("[", "").replace("]", "");
	}
	
	@RequestMapping(value = "/sendjson", method = RequestMethod.POST)
	public String sendMethodWithJson(@RequestBody String chatMessage) throws ParseException{
		
		System.out.println("Base send message called with obj "+chatMessage);
		ChatMessage cms = null;
		CommandInfo cmdInfo = null;

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
									String result = httpHelper.sendGet(MyConstants.SimBaseURL+MyConstants.getassetsbytypeid.replace("tyid", "4"));
									Place place = fromJsontoPlace(ripJsonArray(result));
									msg = place.getValue();
									String jsondata = mapper.writeValueAsString(new CommandInfo("GOD_LOC","CURR",result));
									ChatMessage chatMes = new ChatMessage(ChatMessage.MessageType.CHAT,"GMMSERVER",jsondata);
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
						case "BINS":
							switch(cmdInfo.getQualifier()){
								case "SHOW_FILLED":
									ObjectMapper mapper = new ObjectMapper();
									String result = httpHelper.sendGet(MyConstants.SimBaseURL+MyConstants.getassetsbyvaluegreater.replace("type", "1").replace("rvalue", "75"));
									Place places[] = mapper.readValue(result, Place[].class);
									
									String filledBinNames ="";
									int count = 0;
									for(Place place:places) {
										count++;
										filledBinNames += place.getName()+", ";
									}
									
									List<String> detail = new ArrayList<>();
									detail.add(count+"");
									detail.add(filledBinNames);
									msg = mapper.writeValueAsString(detail);
									//msg = filledBinNames;
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

			} catch (Exception e) {
				e.printStackTrace();
				msg = "Invalid message format.";
			}
			
			System.out.println("Base parsed message ");
			System.out.println("response given is "+msg);
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
	
	public Place fromJsontoPlace(String json) throws Exception {
		Place garima = new ObjectMapper().readValue(json, Place.class);
		return garima;
	}
	
	@Override
	public void finalize() {
		if(sessionHandler!=null) {
			sessionHandler.disconnectFromServer();
		}
	}
	
}


