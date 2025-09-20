package vn.tr.core.client.notification;

import org.springframework.cloud.openfeign.FeignClient;
import vn.tr.common.feign.notification.service.NotificationZaloClient;

@FeignClient(
		name = "notification-service", contextId = "NotificationZaloServiceApi", url = "${client.notification-service.url}"
)
public interface NotificationZaloServiceApi extends NotificationZaloClient {

}
