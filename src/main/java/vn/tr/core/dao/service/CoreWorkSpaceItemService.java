package vn.tr.core.dao.service;

import vn.tr.core.dao.model.CoreWorkSpaceItem;
import vn.tr.core.data.dto.CoreWorkSpaceItemData;

import java.util.Collection;
import java.util.List;

/**
 * Interface cho CoreWorkSpaceItemService, đóng vai trò như một Domain Service.
 * Cung cấp nghiệp vụ "đồng bộ hóa workspace" có thể tái sử dụng.
 *
 * @author tyran8x
 * @version 2.0
 */
public interface CoreWorkSpaceItemService {
	
	/**
	 * Đồng bộ hóa (thêm/sửa/xóa) toàn bộ cây workspace cho một chủ sở hữu.
	 *
	 * @param ownerType       Loại chủ sở hữu (vd: "CoreUser").
	 * @param ownerValue      Giá trị định danh của chủ sở hữu (vd: username).
	 * @param appCode         Mã ứng dụng.
	 * @param newItemDataList Collection các DTO chứa trạng thái mới của workspace.
	 *
	 * @return Danh sách các thực thể CoreWorkSpaceItem đã được đồng bộ hóa.
	 */
	List<CoreWorkSpaceItem> synchronizeWorkspace(
			String ownerType,
			String ownerValue,
			String appCode,
			Collection<CoreWorkSpaceItemData> newItemDataList
	                                            );
	
	/**
	 * Lấy cây workspace đang hoạt động cho một chủ sở hữu.
	 */
	List<CoreWorkSpaceItem> findActiveWorkspaceByOwner(String ownerType, String ownerValue, String appCode);
	
	/**
	 * Kiểm tra xem một item có item con hay không.
	 */
	boolean hasChildren(Long parentId);
}
