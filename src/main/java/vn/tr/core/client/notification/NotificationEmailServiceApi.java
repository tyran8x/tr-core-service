package vn.tr.core.client.notification;

import org.springframework.cloud.openfeign.FeignClient;
import vn.tr.common.feign.notification.service.NotificationEmailClient;

@FeignClient(
		name = "notification-service", contextId = "NotificationEmailServiceApi", url = "${client.notification-service.url}"
)
public interface NotificationEmailServiceApi extends NotificationEmailClient {

}
