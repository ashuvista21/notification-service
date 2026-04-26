package com.ashuvista21.notification.enums;

public enum EventOutboxType {
	CONTACT_VERIFICATION("Event for contact verification"),
    NOTIFICATION("Event for sending notifications"),
    SYSTEM_VERIFICATION("Event for system-level verification") ;
	
	private final String description ;

    EventOutboxType(String description) {
        this.description = description ;
    }

    public String getDescription() {
        return description ;
    }
}
