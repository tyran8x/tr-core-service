package vn.tr.core.client.notification;

import org.springframework.cloud.openfeign.FeignClient;
import vn.tr.common.feign.notification.service.NotificationEmailClient;
import vn.tr.core.config.FeignClientConfig;

@FeignClient(
		name = "notification-service", contextId = "NotificationEmailServiceApi", url = "${client.notification-service.url}",
		configuration = FeignClientConfig.class
)
public interface NotificationEmailServiceApi extends NotificationEmailClient {

}
