package com.example.websocketdemo.model;

public class Place {
	
	private int assetId;
	private int typeId;
	private String value;
	private String name;
	private double latitude;
	private double longitude;
	
	
	
	public int getAssetId() {
		return assetId;
	}



	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}



	public int getTypeId() {
		return typeId;
	}



	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLongitude() {
		return longitude;
	}



	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	
	public Place() {
		
	}

	public Place(int assetId, int typeId, String value, String name, double latitude, double longitude) {
		super();
		this.assetId = assetId;
		this.typeId = typeId;
		this.value = value;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	

}
