package com.okami.bean;

/**
 * 用于放回消息的json数据
 * @author orleven
 * @date 2017年4月30日
 */
public class Message {
	
	private int status;
	
	private String message;
	
	private Object node;
	
	public Message(int status,String message){
		this.status = status;
		this.message = message;
	}
	
	public void setNode(Object node){
		this.node = node;
	}

}
