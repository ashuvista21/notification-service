package com.ashuvista21.notification.dtos;

import java.util.List ;

import lombok.AllArgsConstructor ;
import lombok.Builder ;
import lombok.Getter ;
import lombok.NoArgsConstructor ;
import lombok.Setter ;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationStatusView {
	private String notificationId ;
	private String overallStatus ;
	private String notificationType ;
	private String notificationcategory ;
	private String createdAt ;
	private List<ChannelStatus> channelStatuses ;
	
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ChannelStatus {
		private String channelId ;
		private String channel ;
		private String status ;
		private String errorMessage ;
		private String createdAt ;
		private String sentAt ;
	}
}
