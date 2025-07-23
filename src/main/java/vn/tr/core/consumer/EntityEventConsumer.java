package vn.tr.core.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.tr.common.core.domain.data.CoreContactData;
import vn.tr.common.core.event.EntityUpdatedEvent;
import vn.tr.core.dao.service.CoreContactService;

import java.util.Collection;

/**
 * Lắng nghe các sự kiện cập nhật thực thể chung và điều phối đến các service phù hợp.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityEventConsumer {
	
	private final CoreContactService coreContactService;
	private final ObjectMapper objectMapper;
	
	@KafkaListener(topics = "entity-update-events", groupId = "core-service-entity-consumer")
	public void handleEntityUpdatedEvent(EntityUpdatedEvent event) {
		log.info("Received EntityUpdatedEvent for entityType: {}, entityId: {}", event.getEntityType(), event.getEntityId());
		
		try {
			// **Phân loại sự kiện dựa trên entityType**
			switch (event.getEntityType()) {
				case "CoreUser":
				case "HrmDonVi":
				case "HrmPhongBan":
					// Tất cả các loại này đều có thể có danh bạ
					processContactSynchronization(event);
					break;
				
				// case "SomeOtherEntityType":
				//     someOtherService.process(event);
				//     break;
				
				default:
					log.warn("No handler configured for entityType: {}", event.getEntityType());
			}
		} catch (Exception e) {
			log.error("Failed to process EntityUpdatedEvent for entityId: {}. Error: {}", event.getEntityId(), e.getMessage(), e);
			throw new RuntimeException("Failed to process EntityUpdatedEvent", e);
		}
	}
	
	/**
	 * Helper xử lý nghiệp vụ đồng bộ hóa danh bạ từ một sự kiện chung.
	 */
	private void processContactSynchronization(EntityUpdatedEvent event) {
		// Lấy trường "contacts" từ payload
		if (event.getPayload() == null || !event.getPayload().has("contacts")) {
			log.info("Event for entityType '{}' has no 'contacts' field to synchronize.", event.getEntityType());
			return;
		}
		
		JsonNode contactsNode = event.getPayload().get("contacts");
		
		// Chuyển đổi JsonNode thành Collection<CoreContactData>
		Collection<CoreContactData> contacts = objectMapper.convertValue(contactsNode, new TypeReference<>() {
		});
		
		// Gọi đến service nghiệp vụ
		coreContactService.synchronizeContactsForOwnerInApp(event.getEntityType(), event.getEntityId(), event.getAppCode(), contacts);
	}
}
