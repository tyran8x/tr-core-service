package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;
import vn.tr.core.data.mapper.CoreWorkSpaceItemMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lớp triển khai cho CoreWorkSpaceItemService, chứa logic đồng bộ hóa workspace.
 *
 * @author tyran8x
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoreWorkSpaceItemServiceImpl implements CoreWorkSpaceItemService {
	
	private final CoreWorkSpaceItemRepo coreWorkSpaceItemRepo;
	private final CoreWorkSpaceItemMapper coreWorkSpaceItemMapper;
	
	@Override
	@Transactional
	public List<CoreWorkSpaceItem> synchronizeWorkspace(
			String ownerType,
			String ownerValue,
			String appCode,
			Collection<CoreWorkSpaceItemData> newItemDataList
	                                                   ) {
		log.info("Đồng bộ hóa workspace cho owner='{}', value='{}', app='{}'", ownerType, ownerValue, appCode);
		
		List<CoreWorkSpaceItem> existingItems = coreWorkSpaceItemRepo.findAllByOwnerInAppIncludingDeleted(ownerType, ownerValue, appCode);
		Map<Long, CoreWorkSpaceItem> existingItemMap = existingItems.stream()
				.collect(Collectors.toMap(CoreWorkSpaceItem::getId, Function.identity()));
		
		List<CoreWorkSpaceItem> itemsToSave = new ArrayList<>();
		List<CoreWorkSpaceItem> finalActiveItems = new ArrayList<>();
		
		for (CoreWorkSpaceItemData dto : newItemDataList) {
			CoreWorkSpaceItem item;
			if (dto.getId() != null && existingItemMap.containsKey(dto.getId())) {
				// CẬP NHẬT
				item = existingItemMap.get(dto.getId());
				coreWorkSpaceItemMapper.updateEntityFromData(dto, item);
				if (item.getDeletedAt() != null) {
					item.setDeletedAt(null); // Kích hoạt lại
				}
				existingItemMap.remove(dto.getId());
			} else {
				// THÊM MỚI
				item = coreWorkSpaceItemMapper.toEntity(dto);
				item.setOwnerType(ownerType);
				item.setOwnerValue(ownerValue);
				item.setAppCode(appCode);
			}
			itemsToSave.add(item);
			finalActiveItems.add(item);
		}
		
		// XÓA MỀM các item còn lại trong map
		for (CoreWorkSpaceItem itemToDelete : existingItemMap.values()) {
			if (itemToDelete.getDeletedAt() == null) {
				itemToDelete.setDeletedAt(LocalDateTime.now());
				itemsToSave.add(itemToDelete);
			}
		}
		
		if (!itemsToSave.isEmpty()) {
			coreWorkSpaceItemRepo.saveAll(itemsToSave);
			log.info("Đã lưu {} thay đổi cho workspace của owner='{}', value='{}'", itemsToSave.size(), ownerType, ownerValue);
		}
		
		return finalActiveItems;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CoreWorkSpaceItem> findActiveWorkspaceByOwner(String ownerType, String ownerValue, String appCode) {
		return coreWorkSpaceItemRepo.findByOwnerTypeAndOwnerValueAndAppCodeOrderBySortOrderAsc(ownerType, ownerValue, appCode);
	}
	
	@Override
	public boolean hasChildren(Long parentId) {
		return coreWorkSpaceItemRepo.existsByParentId(parentId);
	}
}
