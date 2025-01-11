package vn.tr.core.data.zalo;

import lombok.Data;

@Data
public class ZaloWebhookData {
	
	private Long app_id;
	
	private Long user_id_by_app;
	
	private String event_name;
	
	private Long timestamp;
	
	private ZaloUserData sender = new ZaloUserData();
	
	private ZaloUserData recipient = new ZaloUserData();
	
	private ZaloMessageData message = new ZaloMessageData();
	
}
