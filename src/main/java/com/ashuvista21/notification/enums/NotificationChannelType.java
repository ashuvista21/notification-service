package com.ashuvista21.notification.enums;

public enum NotificationChannelType {
	SMS(2),
	EMAIL(1),
	WHATSAPP(3),
	PUSH(4) ;
	
	private int priority ;
	
	NotificationChannelType(int priority) {
		this.priority = priority ;
	}
	
	public int getPriority() {
		return this.priority ;
	}
}
