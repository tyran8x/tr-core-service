package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.core.dao.model.CoreUserConnect;
import vn.tr.core.dao.service.CoreUserConnectService;
import vn.tr.core.data.zalo.ZaloMessageData;
import vn.tr.core.data.zalo.ZaloTokenData;
import vn.tr.core.data.zalo.ZaloWebhookData;
import vn.tr.core.enums.AppConnect;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreWebhookBussiness {
	
	private final CoreConfigSystemBusiness coreConfigSystemBusiness;
	
	private final String ZALO_OA_REFRESH_TOKEN = "refresh_token";
	
	private final String ZALO_OA_ACCESS_TOKEN = "access_token";
	
	private final String ZALO_OA_APP_ID = "app_id";
	
	private final String ZALO_OA_SECRET_KEY = "secret_key";
	
	private final String ZALO_OA_GRANT_TYPE = "grant_type";
	
	private final String ZALO_OA_TOKEN_URL = "https://oauth.zaloapp.com/v4/oa/access_token";
	private final CoreUserConnectService coreUserConnectService;
	
	public ZaloTokenData getToken() {
		/* config header */
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.set(ZALO_OA_SECRET_KEY, coreConfigSystemBusiness.getGiaTriByCode(ZALO_OA_SECRET_KEY));
		/* config url */
		
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(ZALO_OA_TOKEN_URL);
		
		MultiValueMap<String, String> mapToken = new LinkedMultiValueMap<>();
		
		mapToken.add(ZALO_OA_REFRESH_TOKEN, coreConfigSystemBusiness.getGiaTriByCode(ZALO_OA_REFRESH_TOKEN));
		mapToken.add(ZALO_OA_APP_ID, coreConfigSystemBusiness.getGiaTriByCode(ZALO_OA_APP_ID));
		mapToken.add(ZALO_OA_GRANT_TYPE, ZALO_OA_REFRESH_TOKEN);
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(mapToken, httpHeaders);
			ResponseEntity<String> responseEntity = restTemplate.exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, entity,
					String.class);
			ZaloTokenData zaloTokenData = new ObjectMapper().readValue(responseEntity.getBody(), new TypeReference<>() {
			});
			// save access token
			coreConfigSystemBusiness.saveConfig(ZALO_OA_ACCESS_TOKEN, zaloTokenData.getAccess_token(), "Zalo");
			// save refesh token
			coreConfigSystemBusiness.saveConfig(ZALO_OA_REFRESH_TOKEN, zaloTokenData.getRefresh_token(), "Zalo");
			return zaloTokenData;
			
		} catch (JsonProcessingException e) {
			log.info("Bug getZaloToken --> {}", e.getMessage());
		}
		
		return null;
	}
	
	public void getMessageZaloOa(ZaloWebhookData zaloWebhookData) {
		ZaloMessageData zaloMessageData = zaloWebhookData.getMessage();
		String text = zaloWebhookData.getMessage().getText();
		String messageId = zaloMessageData.getMsg_id();
		Long userId = zaloWebhookData.getSender().getId();
		Long userIdByApp = zaloWebhookData.getUser_id_by_app();
		String keyOa = messageId + "_" + zaloWebhookData.getUser_id_by_app() + "_" + zaloWebhookData.getTimestamp();
		
		String checkOa = coreConfigSystemBusiness.getGiaTriByCode(keyOa);
		if (StringUtils.isBlank(checkOa)) {
			boolean checkSyntax = false;
			List<String> texts = List.of(text.split(" "));
			if (CollUtil.isNotEmpty(texts) && texts.size() == 2) {
				String syntax = texts.getFirst().trim();
				String userName = texts.getLast().trim();
				String textMessage = "";
				log.info("Check info: {}  - {}", checkSyntax, userName);
				if (StringUtils.equals(syntax, "#DKZALO")) {
					if (StringUtils.isNotBlank(userName)) {
						// gửi tin nhắn đăng ký thành công
						Optional<CoreUserConnect> optionalCoreUserConnect = coreUserConnectService.findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(
								userName, AppConnect.ZALO.getAppConnect());
						if (optionalCoreUserConnect.isPresent()) {
							if (ObjectUtil.equals(userId, optionalCoreUserConnect.get().getAppUserId())) {
								textMessage = "Tài khoản đã được đăng ký!";
							} else {
								textMessage = "Tài khoản đã được đăng ký cho zalo khác, vui lòng kiểm tra lại!";
							}
						} else {
							CoreUserConnect coreUserConnect = new CoreUserConnect();
							coreUserConnect.setAppName(AppConnect.ZALO.getAppConnect());
							coreUserConnect.setAppUserId(userId.toString());
							coreUserConnect.setAppUserIdByApp(userIdByApp.toString());
							coreUserConnect.setUserName(userName);
							coreUserConnectService.save(coreUserConnect);
							textMessage = "Đăng ký thành công!";
						}
					} else {
						// gửi tin nhắn đăng ký không hợp lệ
						textMessage = "Email không hợp lệ vui lòng kiểm tra và thử lại!";
					}
				} else {
					// gửi tin nhắn email không hợp lệ
					textMessage = "Cú pháp không đúng vui lòng thử lại! #DKZALO email_egov";
				}
				sendMessageZalo(userId, messageId, textMessage);
			}
		}
	}

//	private void sendListMessageZalo(Long userId, List<DvcMomoThanhToanListData> dvcMomoThanhToanListDatas) {
//		ZaloTokenData zaloTokenData = getToken();
//		if (Objects.nonNull(zaloTokenData)) {
//			if (CollUtil.isNotEmpty(dvcMomoThanhToanListDatas)) {
//				JSONArray elements = new JSONArray();
//
//				for (DvcMomoThanhToanListData dvcMomoThanhToanListData : dvcMomoThanhToanListDatas) {
//					DvcMomoThanhToanData dvcMomoThanhToanData = dvcMomoThanhToanListData.getDvcMomoThanhToanData();
//					DvcMomoResponse200Data dvcMomoResponse200Data = dvcMomoThanhToanListData.getDvcMomoResponse200Data();
//					JSONObject actionUrl = new JSONObject();
//					actionUrl.set("type", "oa.open.url");
//					actionUrl.set("url", dvcMomoResponse200Data.getPayUrl());
//
//					JSONObject hoSo = new JSONObject();
//					hoSo.set("title", dvcMomoThanhToanData.getOrderInfo() + " - Số tiền: " + dvcMomoThanhToanData.getAmount());
//					hoSo.set("subtitle", "Số tiền: " + dvcMomoThanhToanData.getAmount());
//					hoSo.set("image_url", "https://api.dnict.vn/v1/core/attachment/download/C63EA9E17120DD14E751E201C451296B");
//					hoSo.set("default_action", actionUrl);
//
//					elements.add(hoSo);
//				}
//
//				JSONObject payload = new JSONObject();
//				payload.set("template_type", "list");
//				payload.set("elements", elements);
//
//				JSONObject attachment = new JSONObject();
//				attachment.set("type", "template");
//				attachment.set("payload", payload);
//
//				JSONObject message = new JSONObject();
//				message.set("text", "Danh sách hồ sơ thanh toán");
//				message.set("attachment", attachment);
//
//				JSONObject recipient = new JSONObject();
//				recipient.set("user_id", userId);
//
//				JSONObject body = new JSONObject();
//				body.set("recipient", recipient);
//				body.set("message", message);
//
//				try {
//					/* config header */
//					HttpHeaders httpHeaders = new HttpHeaders();
//					httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//					httpHeaders.set(ZALO_OA_ACCESS_TOKEN, zaloTokenData.getAccess_token());
//					/* config url */
//					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//					HttpEntity<String> requestEntity = new HttpEntity<>(ow.writeValueAsString(body), httpHeaders);
//
//					UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("https://openapi.zalo.me/v2.0/oa/message");
//					String url = uriComponentsBuilder.toUriString();
//					RestTemplate restTemplate = new RestTemplate();
//					ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//					String result = responseEntity.getBody();
//					log.info("result sendListMessageZalo: {}", result);
//				} catch (Exception e) {
//					log.info("Exception sendListMessageZalo: {}", e.getMessage());
//				}
//			}
//		}
//	}
	
	private void sendMessageZalo(Long userId, String messageId, String textMessage) {
		ZaloTokenData zaloTokenData = getToken();
		if (Objects.nonNull(zaloTokenData)) {
			
			JSONObject id = new JSONObject();
			id.set("user_id", userId);
			
			JSONObject text = new JSONObject();
			text.set("text", textMessage);
			
			if (StringUtils.isNotEmpty(messageId)) {
				text.set("react_message_id", messageId);
			}
			
			JSONObject body = new JSONObject();
			body.set("recipient", id);
			body.set("message", text);
			
			try {
				/* config header */
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				httpHeaders.set(ZALO_OA_ACCESS_TOKEN, zaloTokenData.getAccess_token());
				/* config url */
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				HttpEntity<String> requestEntity = new HttpEntity<>(ow.writeValueAsString(body), httpHeaders);
				
				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("https://openapi.zalo.me/v2.0/oa/message");
				String url = uriComponentsBuilder.toUriString();
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				String result = responseEntity.getBody();
				log.info("result sendMessageZalo: {}", result);
			} catch (Exception e) {
				log.info("Exception sendMessageZalo: {}", e.getMessage());
			}
		}
	}
	
	private void getInfoUserQuanTamZalo(Long userId) {
		ZaloTokenData zaloTokenData = getToken();
		if (Objects.nonNull(zaloTokenData)) {
			
			JSONObject data = new JSONObject();
			data.set("user_id", userId);
			
			try {
				/* config header */
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				httpHeaders.set(ZALO_OA_ACCESS_TOKEN, zaloTokenData.getAccess_token());
				/* config url */
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
				
				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("https://openapi.zalo.me/v2.0/oa/message")
						.queryParam("data", data);
				String url = uriComponentsBuilder.toUriString();
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				String result = responseEntity.getBody();
				log.info("result getInfoUserQuanTamZalo: {}", result);
			} catch (Exception e) {
				log.info("Exception getInfoUserQuanTamZalo: {}", e.getMessage());
			}
		}
	}
	
}
