package vn.tr.core.dao.service;

import vn.tr.common.core.domain.data.CoreContactData;
import vn.tr.core.dao.model.CoreContact;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoreContactService {
	
	Optional<CoreContact> findById(Long id);
	
	CoreContact save(CoreContact coreContact);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	/**
	 * Đồng bộ hóa (thêm/sửa/xóa) danh sách liên hệ cho một chủ sở hữu cụ thể trong một ứng dụng.
	 *
	 * @param ownerType       Loại chủ sở hữu (vd: "CoreUser", "HrmDonVi").
	 * @param ownerValue      Giá trị định danh của chủ sở hữu (vd: username, donViId).
	 * @param appCode         Mã ứng dụng.
	 * @param contactDataList Collection các DTO chứa thông tin liên hệ mới.
	 *
	 * @return Danh sách các thực thể CoreContact đã được đồng bộ hóa.
	 */
	List<CoreContact> synchronizeContactsForOwnerInApp(
			String ownerType,
			String ownerValue,
			String appCode,
			Collection<CoreContactData> contactDataList);
	
	/**
	 * Lấy danh sách liên hệ đang hoạt động cho một chủ sở hữu.
	 */
	List<CoreContact> findActiveByOwnerInApp(String ownerType, String ownerValue, String appCode);
	
}
