package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreModule;

import java.util.List;
import java.util.Optional;

public interface CoreModuleService {

	void delete(Long id);

	boolean existsById(Long id);

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	Page<CoreModule> findAll(String search, Boolean trangThai, Pageable pageable);

	List<CoreModule> findByDaXoaFalse();

	Optional<CoreModule> findById(Long id);

	Optional<CoreModule> findByIdAndDaXoaFalse(Long id);

	List<CoreModule> findByIdInAndDaXoaFalse(List<Long> ids);

	List<CoreModule> findByTrangThaiTrueAndDaXoaFalse();

	List<CoreModule> findByIdInAndTrangThaiTrueAndDaXoaFalse(List<Long> ids);

	CoreModule save(CoreModule coreModule);

	int setFixedDaXoaForIds(boolean daXoa, List<Long> ids);

}
