package com.sation.knxcontroller.control;

import java.io.Serializable;

import com.sation.knxcontroller.models.KNXView;

public class KNXConnection extends KNXView implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private int Id; 
	public int getId() {
		return Id;
	} 
	public void setId(int id) {
		Id = id;
	}
	
	private String Name; 
	public String getName() {
		return Name;
	} 
	public void setName(String name) {
		Name = name;
	} 
	
	private String Protocol; 
	public String getProtocol() {
		return Protocol;
	} 
	public void setProtocol(String protocol) {
		Protocol = protocol;
	}
	
	private String Address; 
	public String getAddress() {
		return Address;
	} 
	public void setAddress(String address) {
		Address = address;
	}
	
	private int Port;
	public int getPort() {
		return Port;
	} 
	public void setPort(int port) {
		Port = port;
	} 
}
