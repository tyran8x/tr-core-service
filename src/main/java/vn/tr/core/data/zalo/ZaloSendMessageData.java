package vn.tr.core.data.zalo;

import lombok.Data;

@Data
public class ZaloSendMessageData {
	
	private ZaloRecipientData recipient;
	
	private String text;
	
}
