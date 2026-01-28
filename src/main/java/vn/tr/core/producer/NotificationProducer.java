package vn.tr.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.tr.common.core.constant.NotificationConstants;
import vn.tr.common.core.event.notification.NotificationEmailEvent;
import vn.tr.common.core.event.notification.NotificationSmsEvent;
import vn.tr.common.core.event.notification.NotificationZaloEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmail(NotificationEmailEvent event) {
        log.info("Sending email event to topic {}: {}", NotificationConstants.TOPIC_SEND_EMAIL, event);
        kafkaTemplate.send(NotificationConstants.TOPIC_SEND_EMAIL, event);
    }

    public void sendSms(NotificationSmsEvent event) {
        log.info("Sending sms event to topic {}: {}", NotificationConstants.TOPIC_SEND_SMS, event);
        kafkaTemplate.send(NotificationConstants.TOPIC_SEND_SMS, event);
    }

    public void sendZalo(NotificationZaloEvent event) {
        log.info("Sending zalo event to topic {}: {}", NotificationConstants.TOPIC_SEND_ZALO, event);
        kafkaTemplate.send(NotificationConstants.TOPIC_SEND_ZALO, event);
    }
}
