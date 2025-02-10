package vn.tr.core.data.zalo;

import lombok.Data;

@Data
public class ZaloWebhookData {
	
	private String app_id;
	
	private String user_id_by_app;
	
	private String event_name;
	
	private String timestamp;
	
	private ZaloUserData sender = new ZaloUserData();
	
	private ZaloUserData recipient = new ZaloUserData();
	
	private ZaloMessageData message = new ZaloMessageData();
	
}
