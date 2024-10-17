package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.feign.bean.FileDinhKem;
import vn.tr.common.web.utils.CoreUtils;
import vn.tr.core.dao.model.CoreUserManual;
import vn.tr.core.dao.service.CoreMenuService;
import vn.tr.core.dao.service.CoreRole2MenuService;
import vn.tr.core.dao.service.CoreUserManualService;
import vn.tr.core.data.CoreUserManualData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CoreUserManualBusiness {

	private final CoreUserManualService coreUserManualService;
	private final CoreAttachmentBusiness coreAttachmentBusiness;
	private final CoreMenuService coreMenuService;
	private final CoreRole2MenuService coreRole2MenuService;

	public Page<CoreUserManualData> findAll(int page, int size, String sortBy, String sortDir, String search, Boolean trangThai, String appCode) {
		Pageable pageable = CoreUtils.getPageRequest(page, size, sortBy, sortDir);
		List<String> maUngDungs = new ArrayList<>();
		//		if (!uaaUserService.checkHasRole(RoleConstants.ROLE_ADMIN)) {
		//			TokenUserDetails tokenUserDetails = uaaUserService.getTokenUserDetails();
		//			List<String> roles = new ArrayList<>();
		//			if (Objects.nonNull(tokenUserDetails)) {
		//				roles = tokenUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		//			}
		//			if (CollUtil.isNotEmpty(roles)) {
		//				List<Long> menuIds = coreRole2MenuService.getMenuIds(roles);
		//				List<CoreMenu> coreMenus = coreMenuService.findByIdInAndTrangThaiTrueAndAppCodeAndDaXoaFalse(menuIds, appCode);
		//				maUngDungs = coreMenus.stream().map(CoreMenu::getMa).distinct().toList();
		//			}
		//		}
		Page<CoreUserManual> pageCoreUserManual = coreUserManualService.findAll(search, trangThai, maUngDungs, appCode, pageable);
		return pageCoreUserManual.map(this::convertToCoreUserManualData);
	}

	private CoreUserManualData convertToCoreUserManualData(CoreUserManual coreUserManual) {
		CoreUserManualData coreUserManualData = new CoreUserManualData();
		coreUserManualData.setId(coreUserManual.getId());
		coreUserManualData.setMaUngDung(coreUserManual.getMaUngDung());
		coreUserManualData.setTen(coreUserManual.getTen());
		coreUserManualData.setTrangThai(coreUserManual.getTrangThai());
		coreUserManualData.setSapXep(coreUserManual.getSapXep());
		coreUserManualData.setAppCode(coreUserManual.getAppCode());

		int type = Constants.DINH_KEM_1_FILE;
		Long objectId = coreUserManual.getId();
		String appCode = CoreUserManual.class.getSimpleName();
		FileDinhKem fileDinhKem = coreAttachmentBusiness.getAttachments(coreUserManual.getFileDinhKemId(), appCode, objectId, type);
		coreUserManualData.setFileDinhKem(fileDinhKem);
		coreUserManualData.setFileDinhKemIds(fileDinhKem.getIds());
		return coreUserManualData;
	}

	public CoreUserManualData findById(Long id) throws EntityNotFoundException {
		Optional<CoreUserManual> optional = coreUserManualService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreUserManual.class, id);
		}
		CoreUserManual coreUserManual = optional.get();
		return convertToCoreUserManualData(coreUserManual);
	}

	public CoreUserManualData create(CoreUserManualData coreUserManualData) {
		CoreUserManual coreUserManual = new CoreUserManual();
		return save(coreUserManual, coreUserManualData);
	}

	private CoreUserManualData save(CoreUserManual coreUserManual, CoreUserManualData coreUserManualData) {
		coreUserManual.setDaXoa(false);
		coreUserManual.setMaUngDung(coreUserManualData.getMaUngDung());
		coreUserManual.setTen(coreUserManualData.getTen());
		coreUserManual.setTrangThai(coreUserManualData.getTrangThai());
		coreUserManual.setSapXep(coreUserManualData.getSapXep());
		coreUserManual.setAppCode(coreUserManualData.getAppCode());
		coreUserManual = coreUserManualService.save(coreUserManual);
		/*
		 * Begin đính kèm file
		 *******************************************************/

		/*
		 * Khởi tạo biến
		 * ************************************************************** -
		 * fileDinhKemIds: danh sách id file đã đính kèm
		 * **************************** - type: loại đính kèm (DINH_KEM_1_FILE
		 * || DINH_KEM_NHIEU_FILE) ************* - objectId: id đối tượng đính
		 * kèm ****************************************** - appCode: tên model
		 * của đối tượng đính kèm*********************************
		 */
		List<Long> fileDinhKemIds = coreUserManualData.getFileDinhKemIds();
		int type = Constants.DINH_KEM_1_FILE;
		long objectId = coreUserManual.getId();
		String appCode = CoreUserManual.class.getSimpleName();

		coreUserManual.setFileDinhKemId(null);
		List<Long> fileIds = coreAttachmentBusiness.saveAttachments(fileDinhKemIds, appCode, objectId, type);
		if (CollUtil.isNotEmpty(fileIds)) {
			coreUserManual.setFileDinhKemId(fileIds.getFirst());
			coreUserManualService.save(coreUserManual);
		}
		return convertToCoreUserManualData(coreUserManual);
	}

	public CoreUserManualData update(Long id, CoreUserManualData coreUserManualData) throws EntityNotFoundException {
		Optional<CoreUserManual> optionalCoreUserManual = coreUserManualService.findById(id);
		if (optionalCoreUserManual.isEmpty()) {
			throw new EntityNotFoundException(CoreUserManual.class, id);
		}
		CoreUserManual coreUserManual = optionalCoreUserManual.get();
		return save(coreUserManual, coreUserManualData);
	}

	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreUserManual> optional = coreUserManualService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreUserManual.class, id);
		}
		CoreUserManual coreUserManual = optional.get();
		coreUserManual.setDaXoa(true);
		coreUserManualService.save(coreUserManual);
	}

}
