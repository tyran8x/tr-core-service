package vn.tr.core.client.notification;

import org.springframework.cloud.openfeign.FeignClient;
import vn.tr.common.feign.notification.service.NotificationZaloClient;
import vn.tr.core.config.FeignClientConfig;

@FeignClient(
		name = "notification-service", contextId = "NotificationZaloServiceApi", url = "${client.notification-service.url}",
		configuration = FeignClientConfig.class
)
public interface NotificationZaloServiceApi extends NotificationZaloClient {

}
