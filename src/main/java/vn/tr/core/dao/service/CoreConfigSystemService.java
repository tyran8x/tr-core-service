package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreConfigSystem;

import java.util.List;
import java.util.Optional;

public interface CoreConfigSystemService {

	void deleteById(Long id);

	boolean existsById(Long id);

	Page<CoreConfigSystem> findAll(String maUngDung, String code, Boolean trangThai, Pageable pageable);

	List<CoreConfigSystem> findByDaXoaFalse();

	Optional<CoreConfigSystem> findById(Long id);

	Optional<CoreConfigSystem> findByIdAndDaXoaFalse(Long id);

	List<CoreConfigSystem> findByIdInAndDaXoaFalse(List<Long> ids);

	Optional<CoreConfigSystem> findFirstByCodeAndDaXoaFalse(String code);

	String getGiaTriByCode(String code);

	CoreConfigSystem save(CoreConfigSystem coreConfigSystem);

	int setFixedDaXoaByCode(boolean daXoa, String code);

	int setFixedDaXoaForIds(boolean daXoa, List<Long> ids);

}
