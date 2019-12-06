package com.example.websocketdemo.model;

public class MyConstants {
	
	public static String SimulatorUrl = "https://say-hello-gmm.herokuapp.com/sayhello?name=muthu";
	//public static String SimulatorUrl = "http://wingpsapps.southeastasia.cloudapp.azure.com/bustall/getallassets";
	
	
	public static String SimBaseURL = "http://wingpsapps.southeastasia.cloudapp.azure.com/bustall";
	public static String getassetsbyvaluegreater = "/getassetsbyvaluegreater/type/rvalue";
	public static String getassetsbyvaluesmaller = "/getassetsbyvaluesmaller/type/rvalue";		
	public static String getallassets = "/getallassets";
	public static String getassetsbytypeid = "/getassetsbytypeid/tyid";
	public static String BrokerURL = "ws://gmm-stomp-broker-in-mem.herokuapp.com/ws";
	public static String lBrokerURL = "ws://localhost:5001/ws";
	public static String StompServer = "GMMSERVER";
	
	public static String Bins = "1";
	public static String Camera = "2";
	public static String Tank = "3";
	
	public static String BinThres = "80";
	public static String TankThres = "20";
	
}
