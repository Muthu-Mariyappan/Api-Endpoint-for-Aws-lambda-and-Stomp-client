package com.example.websocketdemo.model;

public class CommandInfo {
	private String command;
	private String Data;
	private String Qualifier;
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getData() {
		return Data;
	}
	public void setData(String data) {
		Data = data;
	}
	public String getQualifier() {
		return Qualifier;
	}
	public void setQualifier(String qualifier) {
		Qualifier = qualifier;
	}
}
