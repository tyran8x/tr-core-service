package vn.tr.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreWebhookBussiness;
import vn.tr.core.data.zalo.ZaloWebhookData;

@RestController
@RequestMapping(value = "/webhook")
@RequiredArgsConstructor
@Slf4j
public class CoreWebHookController {
	
	private final CoreWebhookBussiness coreWebhookBussiness;
	
	@PostMapping("/zalo")
	public R<Void> zalo(@RequestBody ZaloWebhookData zaloWebhookData) {
		//coreWebhookBussiness.getMessageZaloOa(zaloWebhookData);
		return R.ok();
	}
	
}
