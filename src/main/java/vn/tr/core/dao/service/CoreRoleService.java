package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tr.core.dao.model.CoreRole;
import vn.tr.core.data.criteria.CoreRoleSearchCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreRoleService {
	
	Optional<CoreRole> findById(Long id);
	
	CoreRole save(CoreRole coreRole);
	
	void delete(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria, Pageable pageable);
	
	List<CoreRole> findAll(CoreRoleSearchCriteria coreRoleSearchCriteria);
	
	boolean existsByIdNotAndCodeIgnoreCaseAndAppCode(long id, String code, String appCode);
	
	boolean existsByIdNotAndNameIgnoreCaseAndAppCode(long id, String name, String appCode);
	
	boolean existsByCodeIgnoreCaseAndAppCode(String code, String appCode);
	
	boolean existsByNameIgnoreCaseAndAppCode(String name, String appCode);
	
	boolean existsByIdAndAppCode(long id, String appCode);
	
	List<CoreRole> findAllByIds(Collection<Long> ids);
	
	void deleteByIds(Collection<Long> ids);
	
	Optional<CoreRole> findByCodeAndAppCodeIncludingDeleted(String code, String appCode);
	
	JpaRepository<CoreRole, Long> getRepository();
	
	/**
	 * BỔ SUNG: Lọc và trả về một Set các mã vai trò hợp lệ trong một ứng dụng cụ thể.
	 *
	 * @param appCode   Mã ứng dụng.
	 * @param roleCodes Collection các mã vai trò cần kiểm tra.
	 *
	 * @return Một Set chứa các mã vai trò hợp lệ.
	 */
	Set<String> filterExistingRoleCodesInApp(String appCode, Collection<String> roleCodes);
	
	/**
	 * BỔ SUNG: Tìm tất cả các thực thể CoreRole dựa trên appCode và một danh sách các code.
	 *
	 * @param appCode Mã ứng dụng.
	 * @param codes   Collection các mã vai trò.
	 *
	 * @return Danh sách các thực thể CoreRole tìm thấy.
	 */
	List<CoreRole> findAllByAppCodeAndCodeIn(String appCode, Collection<String> codes);
	
	Optional<CoreRole> findByCodeAndAppCode(String code, String appCode);
	
}
