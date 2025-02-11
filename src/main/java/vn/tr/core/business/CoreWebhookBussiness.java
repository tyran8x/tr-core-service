package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.feign.notification.bean.NotificationZaloBean;
import vn.tr.core.client.notification.NotificationZaloServiceApi;
import vn.tr.core.dao.model.CoreUserConnect;
import vn.tr.core.dao.service.CoreUserConnectService;
import vn.tr.core.data.zalo.ZaloMessageData;
import vn.tr.core.data.zalo.ZaloWebhookData;
import vn.tr.core.enums.AppConnect;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreWebhookBussiness {
	
	private final CoreConfigSystemBusiness coreConfigSystemBusiness;
	
	private final CoreUserConnectService coreUserConnectService;
	
	private final NotificationZaloServiceApi notificationZaloServiceApi;
	
	public void getMessageZaloOa(ZaloWebhookData zaloWebhookData) {
		ZaloMessageData zaloMessageData = zaloWebhookData.getMessage();
		String text = zaloWebhookData.getMessage().getText();
		String messageId = zaloMessageData.getMsg_id();
		String userId = zaloWebhookData.getSender().getId();
		String userIdByApp = zaloWebhookData.getUser_id_by_app();
		String keyOa = messageId + "_" + zaloWebhookData.getUser_id_by_app() + "_" + zaloWebhookData.getTimestamp();
		
		String checkOa = coreConfigSystemBusiness.getGiaTriByCode(keyOa, null);
		if (StringUtils.isBlank(checkOa)) {
			boolean checkSyntax = false;
			List<String> texts = List.of(text.split(" "));
			if (CollUtil.isNotEmpty(texts) && texts.size() == 2) {
				String syntax = texts.getFirst().trim();
				String userName = texts.getLast().trim();
				String messageText = "";
				log.info("Check info: {}  - {}", checkSyntax, userName);
				if (StringUtils.equals(syntax, "#DKZALO")) {
					if (StringUtils.isNotBlank(userName)) {
						// gửi tin nhắn đăng ký thành công
						Optional<CoreUserConnect> optionalCoreUserConnect = coreUserConnectService.findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(
								userName, AppConnect.ZALO.getAppConnect());
						if (optionalCoreUserConnect.isPresent()) {
							if (ObjectUtil.equals(userId, optionalCoreUserConnect.get().getAppUserId())) {
								messageText = "Tài khoản đã được đăng ký!";
							} else {
								messageText = "Tài khoản đã được đăng ký cho zalo khác, vui lòng kiểm tra lại!";
							}
						} else {
							CoreUserConnect coreUserConnect = new CoreUserConnect();
							coreUserConnect.setAppName(AppConnect.ZALO.getAppConnect());
							coreUserConnect.setAppUserId(userId);
							coreUserConnect.setAppUserIdByApp(userIdByApp);
							coreUserConnect.setUserName(userName);
							coreUserConnectService.save(coreUserConnect);
							messageText = "Đăng ký thành công!";
						}
					} else {
						// gửi tin nhắn đăng ký không hợp lệ
						messageText = "Email không hợp lệ vui lòng kiểm tra và thử lại!";
					}
				} else {
					// gửi tin nhắn email không hợp lệ
					messageText = "Cú pháp không đúng vui lòng thử lại! #DKZALO email_egov";
				}
				NotificationZaloBean notificationZaloBean = new NotificationZaloBean();
				notificationZaloBean.setUserId(userId);
				notificationZaloBean.setMessageId(messageId);
				notificationZaloBean.setMessageText(messageText);
				notificationZaloServiceApi.send(notificationZaloBean);
			}
		}
	}
	
}
